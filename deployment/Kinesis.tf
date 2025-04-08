
resource "aws_kinesis_stream" "order_stream" {
  name             = "OrderStream-1"  # Stream name
  shard_count      = 1              # Number of shards
  retention_period = 24             # Retention period in hours (1 day = 24 hours)

  # Encryption disabled (default is no encryption)
  encryption_type = "NONE"

  # Optional: Set to ACTIVE if you want to specify the status explicitly (default is active)
  # status = "ACTIVE"  # (no need to include this, as it's active by default)
}

output "stream_arn" {
  value = aws_kinesis_stream.order_stream.arn
}

output "stream_name" {
  value = aws_kinesis_stream.order_stream.name
}