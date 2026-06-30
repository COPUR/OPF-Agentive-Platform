terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "me-central-1" # CBUAE data residency requirement
}

# ---------------------------------------------------------
# Presentation Layer: S3 + CloudFront (Developer Portal)
# ---------------------------------------------------------
resource "aws_s3_bucket" "dev_portal" {
  bucket = "xbank-opf-developer-portal"
}

resource "aws_s3_bucket_public_access_block" "dev_portal_block" {
  bucket                  = aws_s3_bucket.dev_portal.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_cloudfront_distribution" "dev_portal_cdn" {
  origin {
    domain_name = aws_s3_bucket.dev_portal.bucket_regional_domain_name
    origin_id   = "S3-dev-portal"
  }
  
  enabled             = true
  default_root_object = "index.html"
  
  default_cache_behavior {
    target_origin_id       = "S3-dev-portal"
    viewer_protocol_policy = "redirect-to-https"
    allowed_methods        = ["GET", "HEAD"]
    cached_methods         = ["GET", "HEAD"]
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }

  web_acl_id = aws_wafv2_web_acl.opf_waf.arn
}

# ---------------------------------------------------------
# Security: AWS WAF
# ---------------------------------------------------------
resource "aws_wafv2_web_acl" "opf_waf" {
  name        = "opf-edge-protection"
  description = "WAF for OpenFinance Portal"
  scope       = "CLOUDFRONT"
  
  default_action {
    allow {}
  }
  
  visibility_config {
    cloudwatch_metrics_enabled = true
    metric_name                = "opfWafMetrics"
    sampled_requests_enabled   = true
  }
}

# ---------------------------------------------------------
# Backend: Red Hat OpenShift on AWS (ROSA) Cluster Setup
# ---------------------------------------------------------
# NOTE: In a real environment, ROSA clusters are provisioned via the 'rosa' CLI or AWS ROSA TF module.
# Here we define the structural architecture of the node groups to enforce Hexagonal / Decoupled Agentive workloads.

resource "aws_eks_cluster" "rosa_cluster" {
  name     = "opf-rosa-cluster"
  role_arn = aws_iam_role.cluster_role.arn

  vpc_config {
    subnet_ids = [aws_subnet.private_a.id, aws_subnet.private_b.id, aws_subnet.private_c.id]
  }
}

# Node Group 1: Core Transactional Microservices (Hexagonal Architecture)
# These nodes are optimized for synchronous banking APIs.
resource "aws_eks_node_group" "core_microservices" {
  cluster_name    = aws_eks_cluster.rosa_cluster.name
  node_group_name = "core-banking-services"
  node_role_arn   = aws_iam_role.node_role.arn
  subnet_ids      = [aws_subnet.private_a.id, aws_subnet.private_b.id]
  
  scaling_config {
    desired_size = 3
    max_size     = 10
    min_size     = 3
  }
  
  instance_types = ["m6g.xlarge"] # Graviton for cost-performance efficiency
  labels = {
    workload = "core-transactional"
  }
}

# Node Group 2: Decoupled Agentive Workloads (Memory-Intensive)
# Isolated node pool for Temporal workers, preventing them from starving the core APIs.
resource "aws_eks_node_group" "agentive_workloads" {
  cluster_name    = aws_eks_cluster.rosa_cluster.name
  node_group_name = "agentive-cognitive-nodes"
  node_role_arn   = aws_iam_role.node_role.arn
  subnet_ids      = [aws_subnet.private_a.id, aws_subnet.private_b.id]
  
  scaling_config {
    desired_size = 2
    max_size     = 20
    min_size     = 2
  }
  
  # Spot instances optimize costs heavily since Temporal workers are fault-tolerant and idempotent.
  capacity_type  = "SPOT"
  instance_types = ["r6g.2xlarge"] # High memory for embedding generation and cognitive load
  labels = {
    workload = "agentive-cognitive"
  }
}

# IAM Role stub
resource "aws_iam_role" "cluster_role" {
  name = "rosa-cluster-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{ Action = "sts:AssumeRole", Principal = { Service = "eks.amazonaws.com" }, Effect = "Allow" }]
  })
}

resource "aws_iam_role" "node_role" {
  name = "rosa-node-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{ Action = "sts:AssumeRole", Principal = { Service = "ec2.amazonaws.com" }, Effect = "Allow" }]
  })
}

# VPC and Subnet Stubs
resource "aws_vpc" "opf_vpc" { cidr_block = "10.0.0.0/16" }
resource "aws_subnet" "private_a" { vpc_id = aws_vpc.opf_vpc.id; cidr_block = "10.0.1.0/24"; availability_zone = "me-central-1a" }
resource "aws_subnet" "private_b" { vpc_id = aws_vpc.opf_vpc.id; cidr_block = "10.0.2.0/24"; availability_zone = "me-central-1b" }
resource "aws_subnet" "private_c" { vpc_id = aws_vpc.opf_vpc.id; cidr_block = "10.0.3.0/24"; availability_zone = "me-central-1c" }
