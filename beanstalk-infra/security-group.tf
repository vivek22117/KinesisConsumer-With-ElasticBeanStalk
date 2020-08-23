// Security Group for Beanstalk Application
resource "aws_security_group" "rsvp_eb_ec2_sg" {
  name        = "rsvp-eb-ec2-sg"
  description = "Allow inbound traffic from provided Security Groups & ELB"
  vpc_id      = data.terraform_remote_state.vpc.outputs.vpc_id

  lifecycle {
    create_before_destroy = true
  }

  tags = local.common_tags
}


resource "aws_security_group_rule" "allow_traffic_from_bastion_sg" {
  type                     = "ingress"
  from_port                = 22
  to_port                  = 22
  protocol                 = "tcp"
  security_group_id        = aws_security_group.rsvp_eb_ec2_sg.id
  source_security_group_id = data.terraform_remote_state.vpc.outputs.bastion_sg
}

resource "aws_security_group_rule" "allow_traffic_from_lb_sg" {
  type                     = "ingress"
  from_port                = 9009
  to_port                  = 9009
  protocol                 = "tcp"
  security_group_id        = aws_security_group.rsvp_eb_ec2_sg.id
  source_security_group_id = data.terraform_remote_state.vpc.outputs.bastion_sg
}


resource "aws_security_group_rule" "allow_outbound_rule" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  security_group_id = aws_security_group.rsvp_eb_ec2_sg.id
  cidr_blocks       = ["0.0.0.0/0"]
}