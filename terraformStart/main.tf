provider "azurerm" {
    features {}
}
# Create a resource group
resource "azurerm_resource_group" "resource_group" {
    name = "rg-terraform-demo"
    location = "eastus"
}

#get familar with terraform init|plan|apply|destory
