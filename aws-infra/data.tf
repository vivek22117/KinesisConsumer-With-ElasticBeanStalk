//Remote state to fetch s3 deploy bucket
data "terraform_remote_state" "backend" {
  backend = "s3"

  config = {
    profile = "doubledigit"
    bucket  = "${var.s3_bucket_prefix}-${var.environment}-${var.default_region}"
    key     = "state/${var.environment}/aws/terraform.tfstate"
    region  = var.default_region
  }
}

data "terraform_remote_state" "vpc" {
  backend = "s3"

  config {
    profile = "doubledigit"
    bucket  = "${var.s3_bucket_prefix}-${var.environment}-${var.default_region}"
    key     = "state/${var.environment}/vpc/terraform.tfstate"
    region  = var.default_region
  }
}

data "terraform_remote_state" "rsvp_lambda" {
  backend = "s3"

  config {
    profile = "doubledigit"
    bucket  = "${var.s3_bucket_prefix}-${var.environment}-${var.default_region}"
    key     = "state/${var.environment}/vpc/rsvp-lambda-kinesis-db/terraform.tfstate"
    region  = var.default_region
  }
}