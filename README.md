# Setup

## Build the 3 node cluster by running:
```bash
kind create cluster --name lab3 --config kind-multi-node.yaml
```
If already built use commands:
```bash
// To stop the node
docker stop lab3-control-plane lab3-worker lab3-worker2
// To restart the nodes
docker restart lab3-control-plane lab3-worker lab3-worker2
```

## Configuring the cluster
Run:
```bash
cd run-scripts
.\rebuild.ps1
```

## Accessing the Application
Using Port Forwarding to expose the port on your local machine:
```bash
kubectl port-forward svc/client 8081:8081
kubectl port-forward svc/backend 8080:8080
```
****
## Run Python tests by using:
```bash
cd python-testing-scripts
python .\baseline-test.py
python .\circuit_breaker_test.py
python .\retry_test.py
python .\retry-jitter-test.py
```

## Run chaos experiments by 
1. Installing choas toolkit dependencies (virtual environment advised)
2. Running the test scripts
```bash
cd chaos-experiments
chaos run circuit-breaker-test.json
chaos run network-partition.json
chaos run trigger-circuit-breaker.json
chaos run retry-jitter-test.json
chaos run trigger-retry.json
```


**Start**
Quit docker desktop to pause the cluster.
