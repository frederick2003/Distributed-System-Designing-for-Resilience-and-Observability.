# Setup

## Building and Loading Docker Images

**1. Build the images locally**
```bash
cd backendservice
docker build -t backendservice:1.0 .

cd ../clientservice
docker build -t clientservice:1.0 .
```

**Check**
```bash
docker images
```

**2. Load the images into Kind**

```bash
kind load docker-image backendservice:1.0 --name lab3
kind load docker-image clientservice:1.0 --name lab3
```

**3. Deploy to Kubernetes**

```bash
kubectl apply -f backend-deployment.yaml
kubectl apply -f client-deployment.yaml
```

**Verify:**
```bash
kubectl get pods -o wide
kubectl get svc
```


## Accessing the Application
Using Port Forwarding to expose the port on your local machine:
```bash
kubectl port-forward svc/client 8081:8081
kubectl port-forward svc/backend 8080:8080
```
****
Client url: http://localhost:8081/api/request-backend

Backend url: http://localhost:8080/api/data

## Complete workfolow
**Start**
```bash
# 1. Start Docker Desktop
# 2. Recreate or ensure Kind cluster is active
kind get clusters
kind create cluster --name lab3 --config kind-multi-node.yaml  # if missing

# 3. Load images (if rebuilt)
kind load docker-image backendservice:1.0 --name lab3
kind load docker-image clientservice:1.0 --name lab3

# 4. Apply manifests
kubectl apply -f backend-deployment.yaml
kubectl apply -f client-deployment.yaml
```

**Start**
Quit docker desktop to pause the cluster.
