
$ErrorActionPreference = "Stop"
Write-Host "`n Grabbing active Kubectl pods" -ForegroundColor Cyan
kubectl get pods -o wide