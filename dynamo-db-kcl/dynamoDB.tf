resource "aws_dynamodb_table" "rsvp_kcl_lease_table" {
  name = var.db_table_name

  hash_key         = var.hash_key
  billing_mode     = var.billing_mode
  stream_enabled   = var.enable_streams
  stream_view_type = var.stream_view_type

  read_capacity  = var.autoscale_min_read_capacity
  write_capacity = var.autoscale_min_write_capacity

  server_side_encryption {
    enabled = var.enable_encryption
  }

  point_in_time_recovery {
    enabled = var.enable_point_in_time_recovery
  }

  attribute {
    name = var.hash_key
    type = "S"
  }

  tags = merge(local.common_tags, map("Name", "rsvp-db-lease-recorder"))
}