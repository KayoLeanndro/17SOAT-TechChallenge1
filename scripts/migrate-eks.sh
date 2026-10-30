#!/usr/bin/env bash
set -Eeuo pipefail
umask 077
image_uri="${1:?Uso: bash scripts/migrate-eks.sh URL_ECR:TAG}"
render_dir="$(mktemp -d)"
trap 'rm -f -- "$render_dir/job.json"; rmdir -- "$render_dir"' EXIT

kubectl set image --local -f k8s/migration-job.yaml "migrations=$image_uri" -o json > "$render_dir/job.json"
job_name="$(kubectl create --namespace default -f "$render_dir/job.json" -o jsonpath='{.metadata.name}')"
echo "Job de migrations: $job_name; imagem: $image_uri"

diagnose() {
  kubectl logs --namespace default "job/$job_name" --all-containers=true --tail=200 || true
  kubectl get pods --namespace default --selector="job-name=$job_name" || true
}

deadline=$((SECONDS + 660))
read_failures=0
while (( SECONDS < deadline )); do
  if ! status="$(kubectl get job "$job_name" --namespace default -o json)"; then
    read_failures=$((read_failures + 1))
    if (( read_failures >= 3 )); then
      echo "Nao foi possivel confirmar o resultado da migration; deploy bloqueado." >&2
      diagnose
      exit 1
    fi
    sleep 5
    continue
  fi
  read_failures=0
  if jq -e 'any(.status.conditions[]?; .type == "Complete" and .status == "True")' <<< "$status" > /dev/null; then
    kubectl logs --namespace default "job/$job_name" --all-containers=true --tail=200
    echo "Migrations concluidas: $job_name"
    exit 0
  fi
  if jq -e 'any(.status.conditions[]?; .type == "Failed" and .status == "True") or ((.status.failed // 0) > 0)' <<< "$status" > /dev/null; then
    echo "Migration falhou; atualizacao da API bloqueada." >&2
    diagnose
    exit 1
  fi
  sleep 5
done
echo "Tempo esgotado aguardando a migration; deploy bloqueado." >&2
diagnose
exit 1
