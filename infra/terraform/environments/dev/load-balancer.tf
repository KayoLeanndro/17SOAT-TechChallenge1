resource "aws_iam_role" "load_balancer_controller" {
  name = "${var.cluster_name}-load-balancer-controller"

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
          "aws:RequestTag/kubernetes-namespace"       = "kube-system"
          "aws:RequestTag/kubernetes-service-account" = "aws-load-balancer-controller"
        }
      }
    }]
  })
}

# Politica oficial v3.6.2, alinhada a versao do chart em scripts/expose-eks.sh.
resource "aws_iam_policy" "load_balancer_controller" {
  name   = "${var.cluster_name}-load-balancer-controller"
  policy = file("${path.module}/../../../iam/aws-load-balancer-controller-v3.6.2.json")
}

resource "aws_iam_role_policy_attachment" "load_balancer_controller" {
  role       = aws_iam_role.load_balancer_controller.name
  policy_arn = aws_iam_policy.load_balancer_controller.arn
}

resource "aws_eks_pod_identity_association" "load_balancer_controller" {
  cluster_name    = aws_eks_cluster.mechanics_api.name
  namespace       = "kube-system"
  service_account = "aws-load-balancer-controller"
  role_arn        = aws_iam_role.load_balancer_controller.arn

  depends_on = [
    aws_eks_addon.pod_identity_agent,
    aws_iam_role_policy_attachment.load_balancer_controller,
  ]
}

# As sub-redes da VPC default usadas pelo projeto possuem rota para o Internet Gateway.
resource "aws_ec2_tag" "alb_public_subnets" {
  for_each = toset(data.aws_subnets.eks.ids)

  resource_id = each.value
  key         = "kubernetes.io/role/elb"
  value       = "1"
}
