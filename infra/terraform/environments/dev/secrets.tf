locals {
  external_secrets_namespace       = "external-secrets"
  external_secrets_service_account = "external-secrets"
}

# Apenas os metadados sao gerenciados aqui. Cadastre o JSON com os valores
# no Secrets Manager depois do apply, conforme k8s/secrets-manager.md.
resource "aws_secretsmanager_secret" "application" {
  name                    = var.application_secret_name
  description             = "Senha do RDS e chave JWT da mechanics-api dev."
  recovery_window_in_days = 7
}

resource "aws_eks_addon" "pod_identity_agent" {
  cluster_name = aws_eks_cluster.mechanics_api.name
  addon_name   = "eks-pod-identity-agent"

  depends_on = [aws_eks_node_group.mechanics_api]
}

resource "aws_iam_role" "external_secrets" {
  name = "${var.cluster_name}-external-secrets"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "pods.eks.amazonaws.com"
      }
      Action = ["sts:AssumeRole", "sts:TagSession"]
      Condition = {
        StringEquals = {
          "aws:RequestTag/kubernetes-namespace"       = local.external_secrets_namespace
          "aws:RequestTag/kubernetes-service-account" = local.external_secrets_service_account
        }
      }
    }]
  })
}

resource "aws_iam_role_policy" "external_secrets" {
  name = "read-application-secret"
  role = aws_iam_role.external_secrets.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect   = "Allow"
      Action   = ["secretsmanager:GetSecretValue", "secretsmanager:DescribeSecret"]
      Resource = aws_secretsmanager_secret.application.arn
    }]
  })
}

resource "aws_eks_pod_identity_association" "external_secrets" {
  cluster_name    = aws_eks_cluster.mechanics_api.name
  namespace       = local.external_secrets_namespace
  service_account = local.external_secrets_service_account
  role_arn        = aws_iam_role.external_secrets.arn

  depends_on = [
    aws_eks_addon.pod_identity_agent,
    aws_iam_role_policy.external_secrets,
  ]
}
