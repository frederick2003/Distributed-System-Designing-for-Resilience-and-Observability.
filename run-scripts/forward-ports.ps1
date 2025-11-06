$ErrorActionPreference = "Stop"

# Forward the client and backend ports to be visable locally.

kubectl port-forward svc/client 8081:8081
kubectl port-forward svc/backend 8080:8080