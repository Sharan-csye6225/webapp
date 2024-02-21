packer {
  required_plugins {
    googlecompute = {
      source  = "github.com/hashicorp/googlecompute"
      version = "~> 1"
    }
  }
}

variable "gcp_project_id" {
  type    = string
  default = "csye6225-414009"
}

variable "gcp_zone" {
  type    = string
  default = "us-east1-b"
}

variable "ssh_username" {
  type    = string
  default = "sshuser"
}

locals {
  image_family = "custom-image"
}

source "googlecompute" "custom_image_builder" {
  project_id              = var.gcp_project_id
  source_image_family     = "centos-stream-8"
  zone                    = var.gcp_zone
  ssh_username            = var.ssh_username
  network                 = "default"
  image_name              = "csye6225-${formatdate("YYYY-MM-DD-hh-mm-ss", timestamp())}"
  image_description       = "AMI for CSYE 6225"
  image_storage_locations = ["us"]
}

build {
  name    = "build-packer"
  sources = ["source.googlecompute.custom_image_builder"]

  provisioner "shell" {
    script = "install_dependencies.sh"
  }

  provisioner "file" {
    source      = "csye6225-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/csye6225-0.0.1-SNAPSHOT.jar"
  }

  provisioner "file" {
    source      = ".env"
    destination = "/tmp/.env"
  }

  provisioner "file" {
    source      = "webservice.service"
    destination = "/tmp/"
  }

  provisioner "shell" {
    inline = [
      "sudo cp /tmp/csye6225-0.0.1-SNAPSHOT.jar /opt/cloud/csye6225-0.0.1-SNAPSHOT.jar",
      "sudo cp /tmp/.env /opt/cloud/.env",
      "sudo groupadd csye6225",
      "sudo useradd -s /usr/sbin/nologin -g csye6225 -d /opt/cloud -M csye6225",
      "sudo chown -R csye6225:csye6225 /opt/cloud",
      "sudo chmod 750  /opt/cloud/csye6225-0.0.1-SNAPSHOT.jar",
      "cd /opt/cloud/ && ls -al",
      "pwd",
      "sudo cp /tmp/webservice.service /etc/systemd/system",
      "sudo chown 750 /etc/systemd/system/webservice.service",
      "sudo systemctl daemon-reload",
      "sudo systemctl start webservice.service",
      "sudo systemctl enable webservice.service",
      "sudo systemctl restart webservice.service",
      "sudo systemctl status webservice.service",
      "echo '****** Copied webservice *******'"
    ]
  }
}


