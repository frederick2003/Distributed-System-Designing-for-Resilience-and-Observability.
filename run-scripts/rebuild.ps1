# ================================================
# COMP41720 Lab 3 - Automated Build & Deploy Script
# Author: Fred
# ================================================

# Exit on first error
$ErrorActionPreference = "Stop"

Write-Host "`n Rebuilding and redeploying microservices..." -ForegroundColor Cyan

# ------------------------------------------------
# Step 1. Build BackendService with Maven
# ------------------------------------------------

cd ..
Write-Host "`n Building BackendService JAR..." -ForegroundColor Yellow
cd backendservice
mvn clean package -DskipTests

# Step 2. Build Docker image for backend
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$backendTag = "backendservice:$timestamp"
Write-Host "`n Building BackendService Docker image: $backendTag" -ForegroundColor Yellow
docker build -t $backendTag .

cd ..

# Step 3. Load backend image into Kind
Write-Host "`n Loading BackendService image into Kind cluster..." -ForegroundColor Yellow
kind load docker-image $backendTag --name lab3

# Update the deployment YAML to use the new image tag
(Get-Content backend-deployment.yaml) -replace "image: backendservice:.*", "image: $backendTag" | Set-Content backend-deployment.yaml

# ------------------------------------------------
# Step 4. Build ClientService with Maven
# ------------------------------------------------
Write-Host "`n Building ClientService JAR..." -ForegroundColor Yellow
cd clientservice
mvn clean package -DskipTests

# Step 5. Build Docker image for client
$clientTag = "clientservice:$timestamp"
Write-Host "`n Building ClientService Docker image: $clientTag" -ForegroundColor Yellow
docker build -t $clientTag .

cd ..

# Step 6. Load client image into Kind
Write-Host "`n Loading ClientService image into Kind cluster..." -ForegroundColor Yellow
kind load docker-image $clientTag --name lab3
# Update the deployment YAML to use the new image tag
(Get-Content client-deployment.yaml) -replace "image: clientservice:.*", "image: $clientTag" | Set-Content client-deployment.yaml


# ------------------------------------------------
# Step 7. Redeploy to Kubernetes
# ------------------------------------------------
Write-Host "`n☸  Redeploying Kubernetes resources..." -ForegroundColor Yellow

kubectl delete -f backend-deployment.yaml --ignore-not-found
kubectl delete -f client-deployment.yaml --ignore-not-found

kubectl apply -f backend-deployment.yaml
kubectl apply -f client-deployment.yaml

# ------------------------------------------------
# Step 8. Verify deployments
# ------------------------------------------------
Write-Host "`n Checking pods and services..." -ForegroundColor Yellow
kubectl get pods -o wide
kubectl get svc

# ------------------------------------------------
# Step 9. Return back to the run-scripts directory
# ------------------------------------------------

cd run-scripts

Write-Host "`n Redeploy complete! Your latest code is now running in the cluster." -ForegroundColor Green
