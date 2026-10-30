"""Exporta a senha atual do Secrets Manager para o ambiente do GitHub Actions."""
import json
import os
import subprocess
import sys

result = subprocess.run(
    ["aws", "secretsmanager", "get-secret-value", "--secret-id", sys.argv[1], "--output", "json"],
    capture_output=True, text=True, check=False,
)
if result.returncode:
    raise SystemExit("Nao foi possivel ler a senha do Secrets Manager. Confira IAM e o cadastro inicial do segredo.")
password = json.loads(json.loads(result.stdout)["SecretString"]).get("SPRING_DATASOURCE_PASSWORD")
if not isinstance(password, str) or not password or any(c in password for c in "\r\n\0"):
    raise SystemExit("SPRING_DATASOURCE_PASSWORD ausente ou invalida no Secrets Manager.")
mask = password.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")
print("::add-mask::" + mask, flush=True)
with open(os.environ["GITHUB_ENV"], "a", encoding="utf-8") as output:
    output.write("TF_VAR_db_password=" + password + "\n")
print("Senha do Terraform alinhada ao Secrets Manager.")
