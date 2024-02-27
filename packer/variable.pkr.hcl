variable "gcp_project_id" {
  type = string
}

variable "gcp_zone" {
  type = string
}

variable "ssh_username" {
  type = string
}

variable "source_image_family" {
  type = string
}

variable "network" {
  type = string
}

variable "image_name" {
  type = string
}

variable "image_description" {
  type    = string
  default = ""
}

variable "image_storage_locations" {
  type = list(string)
}

variable "image_family" {
  type = string
}

variable "build-name" {
  type = string
}
