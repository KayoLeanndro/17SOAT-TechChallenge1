#!/usr/bin/env bash
# Requer kubeconfig do EKS configurado e Terraform aplicado, incluindo load-balancer.tf.
set -Eeuo pipefail

for tool in aws terraform kubectl helm curl jq; do
  command -v "$tool" >/dev/null || { echo "Ferramenta ausente: $tool" >&2; exit 1; }
done

tf_dir="infra/terraform/environments/dev"
cluster="$(terraform -chdir="$tf_dir" output -raw eks_cluster_name)"
region="$(terraform -chdir="$tf_dir" output -raw aws_region)"
vpc_id="$(terraform -chdir="$tf_dir" output -raw vpc_id)"

helm repo add eks https://aws.github.io/eks-charts --force-update
helm repo update eks
helm upgrade --install aws-load-balancer-controller eks/aws-load-balancer-controller \
  --version 3.6.2 --namespace kube-system \
  --values infra/helm/aws-load-balancer-controller/values.yaml \
  --set-string "clusterName=$cluster" --set-string "region=$region" --set-string "vpcId=$vpc_id" \
  --wait --timeout 5m
kubectl rollout status deployment/aws-load-balancer-controller --namespace kube-system --timeout=300s
kubectl apply -f k8s/ingress.yaml
kubectl wait --namespace default --for=jsonpath='{.status.loadBalancer.ingress[0].hostname}' \
  ingress/mechanics-api --timeout=600s
alb_dns="$(kubectl get ingress mechanics-api --namespace default -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')"
alb_arn="$(aws elbv2 describe-load-balancers --region "$region" --output json \
  | jq -er --arg dns "$alb_dns" '.LoadBalancers[] | select(.DNSName == $dns) | .LoadBalancerArn')"
aws elbv2 wait load-balancer-available --region "$region" --load-balancer-arns "$alb_arn"

# O DNS pode surgir antes de o ALB e seus targets ficarem prontos.
http_get() {
  curl --fail --silent --show-error --retry 24 --retry-delay 5 --retry-all-errors \
    --connect-timeout 5 --max-time 15 "$1"
}
http_get "http://$alb_dns/swagger-ui/index.html" > /dev/null
http_get "http://$alb_dns/v3/api-docs" | jq -e '.openapi' > /dev/null
printf 'API: http://%s/api\nSwagger: http://%s/swagger-ui/index.html\n' "$alb_dns" "$alb_dns"
