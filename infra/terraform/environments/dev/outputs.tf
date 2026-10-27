output "ecr_repository_url" {
  description = "URL do repositório ECR que recebe as imagens da mechanics-api."
  value       = aws_ecr_repository.mechanics_api.repository_url
}

resource "aws_ecr_lifecycle_policy" "mechanics_api" {
  repository = aws_ecr_repository.mechanics_api.name

  policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        description  = "Mantem somente as 5 imagens mais recentes."

        selection = {
          tagStatus   = "any"
          countType   = "imageCountMoreThan"
          countNumber = 5
        }

        action = {
          type = "expire"
        }
      }
    ]
  })
}

output "db_address" {
  description = "Endereco do PostgreSQL, sem a porta."
  value       = aws_db_instance.postgres.address
}

output "application_secret_name" {
  description = "Nome do segredo cujos valores devem ser cadastrados no Secrets Manager."
  value       = aws_secretsmanager_secret.application.name
}

output "application_secret_arn" {
  description = "ARN do segredo que o operador pode ler. Nao contem os valores."
  value       = aws_secretsmanager_secret.application.arn
}

output "external_secrets_role_arn" {
  description = "Role associada ao ServiceAccount do External Secrets Operator via Pod Identity."
  value       = aws_iam_role.external_secrets.arn
}

output "db_port" {
  description = "Porta do PostgreSQL."
  value       = aws_db_instance.postgres.port
}

output "db_name" {
  description = "Nome do banco da aplicacao."
  value       = aws_db_instance.postgres.db_name
}

output "eks_cluster_name" {
  description = "Nome do cluster EKS."
  value       = aws_eks_cluster.mechanics_api.name
}

output "eks_cluster_endpoint" {
  description = "Endpoint da API do Kubernetes."
  value       = aws_eks_cluster.mechanics_api.endpoint
}

output "eks_cluster_security_group_id" {
  description = "Security group do cluster e dos nodes."
  value       = aws_eks_cluster.mechanics_api.vpc_config[0].cluster_security_group_id
}

output "kubeconfig_command" {
  description = "Comando para configurar o kubectl."
  value       = "aws eks update-kubeconfig --region ${var.aws_region} --name ${aws_eks_cluster.mechanics_api.name}"
}
