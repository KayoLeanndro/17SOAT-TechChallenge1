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