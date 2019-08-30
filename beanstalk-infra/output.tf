output "id" {
  description = "ID of the Elastic Beanstalk environment."
  value       = aws_elastic_beanstalk_environment.rsvp_eb_environment.id
}

output "name" {
  value       = aws_elastic_beanstalk_environment.rsvp_eb_environment.name
  description = "Name"
}

output "security_group_id" {
  value       = aws_security_group.rsvp_eb_ec2_sg.id
  description = "Security group id"
}


output "tier" {
  description = "The environment tier specified."
  value       = aws_elastic_beanstalk_environment.rsvp_eb_environment.tier
}

output "application" {
  description = "The Elastic Beanstalk Application specified for this environment."
  value       = aws_elastic_beanstalk_environment.rsvp_eb_environment.application
}

output "setting" {
  description = "Settings specifically set for this environment."
  value       = aws_elastic_beanstalk_environment.rsvp_eb_environment.setting
}

output "all_settings" {
  description = "List of all option settings configured in the environment. These are a combination of default settings and their overrides from setting in the configuration."
  value       = aws_elastic_beanstalk_environment.rsvp_eb_environment.all_settings
}

output "cname" {
  description = "Fully qualified DNS name for the environment."
  value       = aws_elastic_beanstalk_environment.rsvp_eb_environment.cname
}

output "autoscaling_groups" {
  description = "The autoscaling groups used by this environment."
  value       = aws_elastic_beanstalk_environment.rsvp_eb_environment.autoscaling_groups
}

output "instances" {
  description = "Instances used by this environment."
  value       = aws_elastic_beanstalk_environment.rsvp_eb_environment.instances
}

output "launch_configurations" {
  description = "Launch configurations in use by this environment."
  value       = aws_elastic_beanstalk_environment.rsvp_eb_environment.launch_configurations
}