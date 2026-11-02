#!/usr/bin/env bash
# Executar da raiz do repositorio, depois do terraform apply e do push ao ECR.
set -Eeuo pipefail
umask 077

# Leituras podem falhar por DNS/conectividade transitoria mesmo apos um rollout.
retry_check() {
  local attempt
  for attempt in 1 2 3; do
    if "$@"; then return 0; fi
    if [[ "$attempt" != 3 ]]; then sleep 5; fi
  done
  return 1
}

for tool in aws terraform kubectl helm jq sha256sum; do
  command -v "$tool" >/dev/null || { echo "Ferramenta ausente: $tool" >&2; exit 1; }
done

image_uri="${1:?Uso: bash scripts/deploy-eks.sh URL_ECR:TAG}"
tf_dir="infra/terraform/environments/dev"
namespace="default"
render_dir="$(mktemp -d)"
trap 'rm -f -- "$render_dir/"*.json "$render_dir/kubeconfig"; rmdir -- "$render_dir"' EXIT

# Somente outputs publicos; senhas e JWT nunca sao lidos pelo script.
terraform -chdir="$tf_dir" output -json > "$render_dir/outputs.json"
output() {
  jq -er --arg key "$1" '.[$key].value | select(. != null and . != "")' "$render_dir/outputs.json" || {
    echo "Output Terraform ausente: $1. Aplique a infraestrutura com os outputs atuais antes do deploy." >&2
    return 1
  }
}
region="$(output aws_region)"
cluster="$(output eks_cluster_name)"
repository="$(output ecr_repository_url)"
db_address="$(output db_address)"
db_port="$(output db_port)"
db_name="$(output db_name)"
db_username="$(output db_username)"
secret_name="$(output application_secret_name)"

if [[ "$image_uri" != "$repository":* || "$image_uri" == "$repository:" ]]; then
  echo "A imagem precisa usar o repositorio ECR deste ambiente e uma tag publicada." >&2
  exit 1
fi
account_id="$(aws sts get-caller-identity --query Account --output text)"
if [[ "${repository%%.*}" != "$account_id" ]]; then
  echo "A identidade AWS nao pertence a conta do ECR deste ambiente." >&2
  exit 1
fi

# Um kubeconfig temporario impede modificar o contexto local do operador.
if command -v cygpath >/dev/null; then
  # kubectl/AWS CLI nativos do Windows precisam de um caminho Windows no ambiente.
  export KUBECONFIG="$(cygpath -m "$render_dir/kubeconfig")"
else
  export KUBECONFIG="$render_dir/kubeconfig"
fi
aws eks update-kubeconfig --region "$region" --name "$cluster" --kubeconfig "$KUBECONFIG"
kubectl auth can-i patch deployments --namespace "$namespace" | grep -qx yes

helm repo add external-secrets https://charts.external-secrets.io --force-update
helm repo update external-secrets
helm upgrade --install external-secrets external-secrets/external-secrets \
  --version 2.13.0 --namespace external-secrets --create-namespace \
  --values infra/helm/external-secrets/values.yaml --wait --timeout 5m
kubectl wait --for=condition=Established crd/secretstores.external-secrets.io \
  crd/externalsecrets.external-secrets.io --timeout=120s

# A regiao e o nome do segredo acompanham os outputs, mesmo com overrides Terraform.
jq -nc --arg region "$region" '{spec:{provider:{aws:{region:$region}}}}' > "$render_dir/store-patch.json"
jq -nc --arg key "$secret_name" '[{op:"replace",path:"/spec/data/0/remoteRef/key",value:$key},{op:"replace",path:"/spec/data/1/remoteRef/key",value:$key}]' > "$render_dir/secret-patch.json"
kubectl patch --local -f k8s/secret-store.yaml --type merge \
  --patch-file "$render_dir/store-patch.json" \
  -o json > "$render_dir/secret-store.json"
kubectl patch --local -f k8s/external-secret.yaml --type json \
  --patch-file "$render_dir/secret-patch.json" \
  -o json > "$render_dir/external-secret.json"
kubectl apply --namespace "$namespace" -f "$render_dir/secret-store.json"
kubectl wait --for=condition=Ready secretstore/mechanics-api-aws --namespace "$namespace" --timeout=180s
kubectl apply --namespace "$namespace" -f "$render_dir/external-secret.json"
kubectl wait --for=condition=Ready externalsecret/mechanics-api --namespace "$namespace" --timeout=180s
kubectl get secret mechanics-api-secrets --namespace "$namespace" -o name

kubectl create configmap mechanics-api-config --namespace "$namespace" \
  --from-literal="SPRING_DATASOURCE_URL=jdbc:postgresql://$db_address:$db_port/$db_name" \
  --from-literal="SPRING_DATASOURCE_USERNAME=$db_username" \
  --from-literal="SERVER_PORT=8080" --dry-run=client -o json > "$render_dir/configmap.json"
kubectl apply -f "$render_dir/configmap.json"

# A mesma imagem migra o banco antes de substituir qualquer pod da API.
bash scripts/migrate-eks.sh "$image_uri"

# Alteracoes de ConfigMap tambem substituem os pods, inclusive ao repetir a mesma tag.
config_hash="$(sha256sum "$render_dir/configmap.json" | cut -d ' ' -f 1)"
kubectl set image --local -f k8s/deployment.yaml "mechanics-api=$image_uri" -o json \
  | jq --arg hash "$config_hash" '.spec.template.metadata.annotations["mechanics-api/config-hash"] = $hash' \
  > "$render_dir/deployment.json"
kubectl apply --namespace "$namespace" -f "$render_dir/deployment.json" -f k8s/service.yaml -f k8s/hpa.yaml
kubectl rollout status deployment/mechanics-api --namespace "$namespace" --timeout=600s
retry_check kubectl wait --for=condition=Available apiservice/v1beta1.metrics.k8s.io --timeout=180s
retry_check kubectl wait --for=condition=ScalingActive hpa/mechanics-api --namespace "$namespace" --timeout=300s
bash scripts/expose-eks.sh
kubectl get deployment,pods,service,hpa --namespace "$namespace" -l app=mechanics-api
