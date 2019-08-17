resource "aws_elastic_beanstalk_application" "rsvp_eb_application" {
  name = "rsvp-event-processor-${var.environment}"
}

resource "aws_elastic_beanstalk_application_version" "rsvp_eb_version" {
  description = "version of new deployment"
  application = "${aws_elastic_beanstalk_application}-Version-0.0.1"
  bucket = data.terraform_remote_state.backend.deploy_bucket_name
  key = var.deploy_key
  name = aws_elastic_beanstalk_application.rsvp_eb_application.name
}

resource "aws_elastic_beanstalk_environment" "rsvp_eb_environment" {
  application = aws_elastic_beanstalk_application.rsvp_eb_application.name
  name = var.environment
  solution_stack_name = "64bit Amazon Linux 2018.03 v3.0.7 running Tomcat 8 Java 8"
  version_label = aws_elastic_beanstalk_application_version.rsvp_eb_version.name
  tier = var.tier

  wait_for_ready_timeout = var.wait_for_ready_timeout

  tags = local.common_tags

  lifecycle {
    ignore_changes = ["tags"]
  }

  setting {
    namespace = "aws:ec2:vpc"
    name      = "VPCId"
    value     = data.terraform_remote_state.vpc.vpc_id
  }

  setting {
    namespace = "aws:ec2:vpc"
    name      = "AssociatePublicIpAddress"
    value     = var.associate_public_ip_address
  }

  setting {
    namespace = "aws:ec2:vpc"
    name      = "Subnets"
    value     = join(",", data.terraform_remote_state.vpc.private_subnets)
  }

  setting {
    namespace = "aws:ec2:vpc"
    name      = "ELBSubnets"
    value     = join(",", data.terraform_remote_state.vpc.public_subnets)
  }

  setting {
    namespace = "aws:ec2:vpc"
    name = "ELBScheme"
    value = var.elb_scheme
  }

  setting {
    namespace = "aws:elasticbeanstalk:command"
    name = "DeploymentPolicy"
    value = var.deployment_policy
  }

  setting {
    namespace = "aws:elasticbeanstalk:command"
    name = "Timeout"
    value = var.eb_command_timeout
  }

  setting {
    namespace = "aws:elasticbeanstalk:command"
    name = "BatchSize"
    value = var.deployment_batch_size
  }
  setting {
    namespace = "aws:elasticbeanstalk:command"
    name = "BatchSizeType"
    value = var.dp_batch_size_type
  }

  setting {
    namespace = "aws:autoscaling:updatepolicy:rollingupdate"
    name = "RollingUpdateEnabled"
    value = var.rolling_update_enabled
  }

  setting {
    namespace = "aws:autoscaling:updatepolicy:rollingupdate"
    name = "Timeout"
    value = var.asg_timeout
  }

  setting {
    namespace = "aws:autoscaling:updatepolicy:rollingupdate"
    name = "RollingUpdateType"
    value = var.rolling_update_type
  }

  setting {
    namespace = "aws:elasticbeanstalk:healthreporting:system"
    name = "SystemType"
    value = var.enhanced_reporting_enabled ? "enhanced" : "basic"
  }

  setting = {
    namespace = "aws:elasticbeanstalk:healthreporting:system"
    name = "HealthCheckSuccessThreshold"
    value = "Ok"
  }

  setting {
    namespace = "aws:elasticbeanstalk:healthreporting:system"
    name = "ConfigDocument"
    value = var.config_document
  }

  ###===================== Application Load Balancer Health check settings =====================================###
  setting {
    namespace = "aws:elasticbeanstalk:environment:process:default"
    name      = "HealthCheckInterval"
    value     = var.healthcheck_interval
  }

  setting {
    namespace = "aws:elasticbeanstalk:environment:process:default"
    name      = "HealthCheckPath"
    value     = var.healthcheck_url
  }

  setting {
    namespace = "aws:elasticbeanstalk:environment:process:default"
    name      = "HealthCheckTimeout"
    value     = var.healthcheck_timeout
  }

  setting {
    namespace = "aws:elasticbeanstalk:environment:process:default"
    name      = "HealthyThresholdCount"
    value     = var.hc_threshold_count
  }

  setting {
    namespace = "aws:elasticbeanstalk:environment:process:default"
    name      = "Port"
    value     = var.application_port
  }
  setting {
    namespace = "aws:elasticbeanstalk:environment:process:default"
    name      = "Protocol"
    value     = "HTTP"
  }

  ###=========================== Autoscale & LaunchConfig========================== ###

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name = "IamInstanceProfile"
    value = aws_iam_instance_profile.rsvp_beanstalk_ec2_profile.name
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name = "EC2KeyName"
    value = var.key_pair
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name = "ImageId"
    value = var.instance_ami
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name = "InstanceType"
    value = var.instance_type
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name = "RootVolumeSize"
    value = var.root_volume_size
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name = "RootVolumeType"
    value = var.root_volume_type
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name = "MonitoringInterval"
    value = var.monitorning_interval
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "SSHSourceRestriction"
    value     = "tcp,22,22,${data.terraform_remote_state.vpc.outputs.bastion_sg}"
  }

  setting {
    namespace = "aws:autoscaling:asg"
    name = "MinSize"
    value = var.autoscale_min
  }

  setting {
    namespace = "aws:autoscaling:asg"
    name = "MaxSize"
    value = var.autoscale_max
  }

  setting {
    namespace = "aws:autoscaling:asg"
    name      = "Availability Zones"
    value     = var.availability_zones
  }

  ###=========================== Autoscale trigger ========================== ###

  setting {
    namespace = "aws:autoscaling:trigger"
    name      = "MeasureName"
    value     = var.autoscale_measure_name
  }
  setting {
    namespace = "aws:autoscaling:trigger"
    name      = "Statistic"
    value     = var.autoscale_statistic
  }
  setting {
    namespace = "aws:autoscaling:trigger"
    name      = "Unit"
    value     = var.autoscale_unit
  }
  setting {
    namespace = "aws:autoscaling:trigger"
    name      = "LowerThreshold"
    value     = var.autoscale_lower_bound
  }
  setting {
    namespace = "aws:autoscaling:trigger"
    name      = "LowerBreachScaleIncrement"
    value     = var.scale_down_value
  }
  setting {
    namespace = "aws:autoscaling:trigger"
    name      = "UpperThreshold"
    value     = var.autoscale_upper_bound
  }
  setting {
    namespace = "aws:autoscaling:trigger"
    name      = "UpperBreachScaleIncrement"
    value     = var.scale_up_value
  }

  setting {
    namespace = "aws:elbv2:listener:80"
    name = "Protocol"
    value = "HTTP"
  }

  setting {
    namespace = "aws:elbv2:listener:80"
    name = "ListenerEnabled"
    value = "true"
  }

  setting {
    namespace = "aws:elbv2:loadbalancer"
    name = "IdleTimeout"
    value = "300"
  }


  ###===================== Application EB ENV vars ======================###

  setting {
    namespace = "aws:elasticbeanstalk:application"
    name      = "Application Healthcheck URL"
    value     = "HTTP:${var.application_port}${var.healthcheck_url}"
  }

  setting {
    namespace = "aws:elasticbeanstalk:environment"
    name      = "EnvironmentType"
    value     = var.eb_environment
  }

  setting {
    namespace = "aws:elasticbeanstalk:environment"
    name = "ServiceRole"
    value = aws_iam_role.rsvp_beanstalk_service_role.name
  }

  setting {
    namespace = "aws:elasticbeanstalk:environment"
    name = "LoadBalancerType"
    value = var.loadbalancer_type
  }

  ###===================== Application ENV vars ======================###

  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name = "RUNTIME_ENVIRONMENT"
    value = var.environment
  }

  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name = "KINESIS_STREAM"
    value = data.terraform_remote_state.rsvp_lambda.kinesis_arn
  }

  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name = "DYNAMODB_TABLE"
    value = data.terraform_remote_state.rsvp_lambda.dynamodb_table
  }
}


data "aws_lb" "rsvp_alb" {
  arn = aws_elastic_beanstalk_environment.rsvp_eb_environment.load_balancers[0]
}

data "aws_autoscaling_group" "rsvp_asg" {
  name = aws_elastic_beanstalk_environment.rsvp_eb_environment.autoscaling_groups[0]
}

data "aws_lb_target_group" "rspv_alb_tg" {
  arn = data.aws_autoscaling_group.rsvp_asg.target_group_arns[0]
}

resource "aws_lb_listener" "http_to_http_redirect" {
  load_balancer_arn = data.aws_lb.rsvp_alb.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type = "redirect"

    redirect {
      port        = "5500"
      protocol    = "HTTP"
      status_code = "HTTP_200"
    }
  }
}

resource "aws_lb_listener_rule" "http_root_redirect_to_index_page_v1" {
  listener_arn = aws_lb_listener.http_to_http_redirect.arn

  action {
    type = "redirect"

    redirect {
      port        = "5500"
      protocol    = "HTTP"
      status_code = "HTTP_200"
      path        = "/acutator/health"
    }
  }

  condition {
    field  = "path-pattern"
    values = ["/"]
  }
}


