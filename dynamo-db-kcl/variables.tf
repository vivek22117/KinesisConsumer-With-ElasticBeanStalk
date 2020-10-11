//Global Variables
variable "profile" {
  type        = "string"
  description = "AWS Profile name for credentials"
}

variable "environment" {
  type        = "string"
  description = "Environment to deploy, Valid values 'qa', 'dev', 'prod'"
}


//Default Variables
variable "default_region" {
  type    = "string"
}

variable "s3_bucket_prefix" {
  type    = "string"
}

//EB Default Variables
variable "wait_for_ready_timeout" {
  type = string
  description = "The maximum time that Terraform should wait for an Elastic Beanstalk Environment"
}

variable "config_document" {
  type = string
  description = "A JSON document describing the environment and instance metrics to publish to CloudWatch."
}

//Dynamic Variables
variable "tier" {
  type = string
  description = "Beanstalk environment tier {WebServer, Worker}"
}

variable "deploy_key" {
  type = "string"
  description = "S3 key path for beanstalk application jar"
}

variable "eb_environment" {
  type = string
  description = "Environment type, e.g. 'LoadBalanced' or 'SingleInstance'"
}

variable "eb_command_timeout" {
  type = number
  description = "Number of seconds to wait for an instance to complete executing commands + 240"
}

variable "deployment_batch_size" {
  type = number
  description = "Percentage or fixed number of Amazon EC2 instances"
}

variable "dp_batch_size_type" {
  type = string
  description = "The type of number that is specified in BatchSize."
}

variable "ssh_listener_enabled" {
  type     = string
  description = "Enable ssh port"
}

variable "ssh_listener_port" {
  type     = string
  description = "SSH port"
}

variable "associate_public_ip_address" {
  type = "string"
  description = "allow public ip address in ec2"
}

variable "rolling_update_type" {
  type = "string"
  description = "Rolling update type like Health"
}

variable "deployment_policy" {
  type = "string"
  description = "Deployment policy for EB stalk Immutable or Rolling"
}

variable "rolling_update_enabled" {
  type = "string"
  description = "Is rolling update enbaled or not"
}

variable "key_pair" {
  type = "string"
  description = "Name of SSH key that will be deployed on Elastic Beanstalk"
}

variable "root_volume_size" {
  type     = "string"
  description = "The size of the EBS root volume"
}

variable "root_volume_type" {
  type     = "string"
  description = "The type of the EBS root volume"
}

variable "instance_ami" {
  type = string
  description = "Amazon machine image to launch EC2"
}

variable "instance_type" {
  type     = "string"
  description = "Instances type"
}

variable "enhanced_reporting_enabled" {
  type = "string"
  description = "Is enhanced reporting enabled or not for EB"
}

variable "healthcheck_url" {
  type = "string"
  description = "Health check url path"
}

variable "application_port" {
  type = "string"
  description = "Port application is listening on"
}

variable "autoscale_min" {
  type     = "string"
  description = "Minumum instances in charge"
}

variable "autoscale_max" {
  type     = "string"
  description = "Maximum instances in charge"
}

variable "availability_zones" {
  type = string
  description = "Choose the number of AZs for your instances"
}

variable "autoscale_measure_name" {
  type = string
  description = "Metric used for your Auto Scaling trigger"
}

variable "autoscale_statistic" {
  type = string
  description = "Statistic the trigger should use, such as Averag, Maximum, Minimum"
}

variable "autoscale_unit" {
  type = string
  description = "Unit for the trigger measurement, such as Bytes"
}

variable "autoscale_lower_bound" {
  type = number
  description = "Minimum level of autoscale metric to remove an ec2 instance"
}

variable "scale_down_value" {
  type = number
  description = "Number of EC2 instances to remove when performing a scaling activity"
}

variable "autoscale_upper_bound" {
  type = number
  description = "Maximum level of autoscale metric to add new ec2 instance"
}

variable "scale_up_value" {
  type = number
  description = "Number of EC2 instances to add when performing a scaling activity"
}

variable "monitoring_interval" {
  type = string
  description = "Time interval for AWS Cloudwatch metrics"
}

variable "asg_timeout" {
  type = string
  description = "Maximum amount of time to wait for all instances in a batch of instances to pass health checks before canceling the update."
}

variable "healthcheck_interval" {
  type = number
  description = "The interval, in seconds, at which Elastic Load Balancing will check the health"
}

variable "healthcheck_timeout" {
  type = number
  description = "Time, in seconds, to wait for a response during a health check."
}

variable "hc_threshold_count" {
  type = number
  description = "Consecutive successful requests before Elastic Load Balancing changes the instance health status."
}

###====================KCL DynamoDB Variables=====================###
variable "db_table_name" {
  type        = "string"
  description = "DynamoDB table"
}

variable "hash_key" {
  type        = "string"
  description = "DynamoDB table hash key"
}

variable "billing_mode" {
  type        = "string"
  default     = "PROVISIONED"
  description = "DynamoDB Billing mode. Can be PROVISIONED or PAY_PER_REQUEST"
}

variable "enable_streams" {
  type        = "string"
  default     = "false"
  description = "Enable DynamoDB streams"
}

variable "stream_view_type" {
  type        = "string"
  default     = ""
  description = "When an item in the table is modified, what information is written to the stream KEYS_ONLY, NEW_IMAGE, OLD_IMAGE, NEW_AND_OLD_IMAGES."
}

variable "autoscale_min_read_capacity" {
  default     = 2
  description = "DynamoDB autoscaling min read capacity"
}

variable "autoscale_min_write_capacity" {
  default     = 2
  description = "DynamoDB autoscaling min write capacity"
}

variable "enable_encryption" {
  type        = "string"
  default     = "false"
  description = "Enable DynamoDB server-side encryption"
}

variable "enable_point_in_time_recovery" {
  type        = "string"
  default     = "false"
  description = "Enable DynamoDB point in time recovery"
}

//Local variables
locals {
  common_tags = {
    owner       = "Vivek"
    team        = "TeamConcept"
    environment = var.environment
    Project = "DoubleDigit-Solutions"
  }
}