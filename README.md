# Enviroment Requirement
install az cli
install az install bicep
install [terraform]

# create and delete ResourceGroup using Azure CLI
az group create --n MyResourceGroup -l eastus 
az group delete --name MyResourceGroup

# create and delete ResourceGroup using Terraform

terraform init->plan->apply->destroy

# create static webSite using Terraform
cd terraformInfra
terraform init|plan|apply|destory

# create VM using Terraform
cd terraformVM
terraform init|plan|apply|destory

# operate Azure using bicep
cd bicep

# create vm by Deploy the Bicep File with Parameters Using Azure CLI

az deployment group create -g bicep -f .\createVM.bicep --parameters=parameters.json

# delete vm by Deploy the Modified Template in Complete Mode

az deployment group create -g bicep -f .\deleteVM.bicep --mode Complete

# Hints : save cost during using VM on azure
1. stop vm and then deallocate it
2. remove Public IP or reduce public IP usage
3. setup remote control via HBsys then remove public IP 








