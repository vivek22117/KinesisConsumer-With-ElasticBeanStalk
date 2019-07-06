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

//EB Variables
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

variable "elb_scheme" {
  default     = "internal"
  description = "Specify if load balancer in your Amazon VPC so that your Elastic Beanstalk application cannot be accessed from outside"
}

variable "loadbalancer_certificate_arn" {
  default     = ""
  description = "Load Balancer SSL certificate ARN."
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

variable "loadbalancer_type" {
  type     = "string"
  description = "Load Balancer type, e.g. 'application' or 'classic'"
}
//Local variables
locals {
  common_tags = {
    owner       = "Vivek"
    team        = "TeamConcept"
    environment = var.environment
  }
}