// Security Group for Beanstalk Application
resource "aws_security_group" "rsvp_eb_sg" {
  name        = "rsvp-eb-sg"
  description = "Allow inbound traffic from provided Security Groups"
  vpc_id      = data.terraform_remote_state.vpc.vpc_id

  lifecycle {
    create_before_destroy = true
  }

  tags = local.common_tags
}


resource "aws_security_group_rule" "allow_traffic_from_sg" {
  type                     = "ingress"
  from_port                = 0
  to_port                  = 0
  protocol                 = "-1"
  security_group_id        = aws_security_group.rsvp_eb_sg.id
  source_security_group_id = data.terraform_remote_state.vpc.bastion_sg
}

resource "aws_security_group_rule" "allow_outbound_rule" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  security_group_id = aws_security_group.rsvp_eb_sg.id
  cidr_blocks       = ["0.0.0.0/0"]
}