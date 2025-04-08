
# Terraform required providers and their versions
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"  # Ensure that the latest major version is used
    }
  }

  required_version = ">= 1.0"  # Ensure that Terraform version 1.x or higher is used
}


