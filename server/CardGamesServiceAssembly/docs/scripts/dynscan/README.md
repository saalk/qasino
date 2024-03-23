# Setting up dynscan for your application
Created using [dynscan page from mobile](https://confluence.ing.net/display/IAC/Automated+Pentests+-+DynScan+-+Vijay) as reference

## Step 0: double check security champions
Double check if all security champions in your team are listed in the [dynscan template](https://dev.azure.com/INGCDaaS/IngOne/_git/P00451-credit-cards-toolkit?path=/credit-cards-azure/dynscan.yml) and update if required

## Step 1: expose endpoints
Configure all ngnix facing endpoints to the `pt.ing.net` domain (same [form](https://gateway.ing.net/configuration/touchpoint-endpoints-request) as for ming and iris)

## Step 2: alter files in dynscan folder
* The [environment file](CreditCards_ACCP_PT.postman_environment.json) does not need change, unless you want it to test with different user
* Alter the [endpoint description file](CreditCardsRequestExampleService.json) to include all NGINX facing endpoints
  * Ensure that `/authorize` and other non-NGINX facing endpoints are not in there
* Alter the [postman collection](CreditCardsRequestExampleService.postman_collection.json) to call each endpoint in scope
  * They should have tests asserting the output such as http 200 and requestId in response body

## Step 3: run the pipeline
The pipeline config is already supplied in this project, after deploying to acceptance the scan runs automatically

## Step 4: check the results
Open the scan result `Dynscan-Report.html` from the artifact `Dynscan` in the pipeline.
Review the findings and approve (listed security champions only) the pipeline to allow production deployments