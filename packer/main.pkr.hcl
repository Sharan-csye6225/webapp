packer {
  required_plugins {
    googlecompute = {
      source  = "github.com/hashicorp/googlecompute"
      version = "~> 1"
    }
  }
}

source "googlecompute" "custom_image_builder" {
  project_id              = var.gcp_project_id
  source_image_family     = var.source_image_family
  zone                    = var.gcp_zone
  ssh_username            = var.ssh_username
  network                 = var.network
  image_name              = "${var.image_name}-${formatdate("YYYY-MM-DD-hh-mm-ss", timestamp())}"
  image_description       = var.image_description
  image_storage_locations = var.image_storage_locations
}

locals {
  image_family = "custom-image"
}

build {
  name    = var.build_name
  sources = ["source.googlecompute.custom_image_builder"]

  provisioner "shell" {
    script = "installation_dependencies.sh"
  }

  provisioner "file" {
    source      = "csye6225-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/csye6225-0.0.1-SNAPSHOT.jar"
  }

  provisioner "file" {
    source      = "webservice.service"
    destination = "/tmp/"
  }

  provisioner "shell" {
    script = "nologin-user-creation.sh"
  }

}


