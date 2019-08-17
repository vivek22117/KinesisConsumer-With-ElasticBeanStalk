//Global Variables
variable "profile" {
  type        = "string"
  description = "AWS Profile name for credentials"
}

variable "environment" {
  type        = "string"
  description = "AWS Profile name for credentials"
}


//Default Variables
variable "default_region" {
  type    = "string"
  default = "us-east-1"
}

variable "s3_bucket_prefix" {
  type    = "string"
  default = "teamconcept-tfstate"
}

//EB Default Variables
variable "tier" {
  default = "WebServer"
  description = "Beanstalk environment tier {WebServer, Worker}"
}

variable "deploy_key" {
  type = "string"
  description = "S3 key path for beanstalk application jar"
  default = "eb/rspv-event-processor/rsvp-*.jar"
}

variable "wait_for_ready_timeout" {
  default     = "15m"
  description = "The maximum time that Terraform should wait for an Elastic Beanstalk Environment"
}

variable "config_document" {
  default     = "{ \"CloudWatchMetrics\": {}, \"Version\": 1}"
  description = "A JSON document describing the environment and instance metrics to publish to CloudWatch."
}

variable "eb_environment" {
  type = string
  description = "Environment type, e.g. 'LoadBalanced' or 'SingleInstance'"
}
variable "elb_scheme" {
  type     = string
  description = "Specify if load balancer in your Amazon VPC so that your Elastic Beanstalk application cannot be accessed from outside"
}

variable "loadbalancer_certificate_arn" {
  default     = ""
  description = "Load Balancer SSL certificate ARN."
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
  default     = "false"
  description = "Enable ssh port"
}

variable "ssh_listener_port" {
  default     = "22"
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

variable "loadbalancer_type" {
  type     = "string"
  description = "Load Balancer type, e.g. 'application' or 'classic'"
}


variable "monitorning_interval" {
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
//Local variables
locals {
  common_tags = {
    owner       = "Vivek"
    team        = "TeamConcept"
    environment = var.environment
  }
}