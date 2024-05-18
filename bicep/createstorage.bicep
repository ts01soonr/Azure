//-------------
//az deployment group create -f ./main.bicep -g rg-start-es1
//--delete storageaccount
//az storage account delete -g rg-start-es1 -n fangdemostorage3
//--mode Complete 
//az deployment group create -g exampleGroup --mode Complete -f .\createstorage.bicep

resource storageAccount 'Microsoft.Storage/storageAccounts@2022-09-01' = {
  name: 'fangdemostorage4mode'
  location: 'eastus'
  sku: {
    name: 'Standard_LRS'
  }
  kind: 'StorageV2'
  properties: {
    accessTier: 'Hot'
  }
}
