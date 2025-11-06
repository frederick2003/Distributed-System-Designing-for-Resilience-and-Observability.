import requests
import time

CLIENT_URL = "http://localhost:8081/api/test-circuit"
BACKEND_FAILURE_CONFIG_URL = "http://localhost:8080/config/failure"
BACKEND_LATENCY_CONFIG_URL = "http://localhost:8080/config/latency"
BACKEND_PUT_URL = "http://localhost:8080/api.data"



def set_backend_failure(failure_rate, error_code=500):
    """Configure backend failure behaviour."""
    data = {"failure_rate": failure_rate, "error_code": error_code}
    try:
        r = requests.post(BACKEND_FAILURE_CONFIG_URL, json=data)
        print(f"[Backend Config] {r.text}")
    except Exception as e:
        print(f"Failed to configure backend: {e}")

def call_client_service(times=10, delay=0.5):
    """Call the client endpoint multiple times."""
    for i in range(times):
        try:
            r = requests.get(CLIENT_URL, timeout=3)
            print(f"Request {i+1:02d}: {r.text}")
        except Exception as e:
            print(f"Request {i+1:02d} failed: {e}")
        time.sleep(delay)

def main():
    print("\n=== Phase 1: Normal operation (backend healthy) ===")
    set_backend_failure(0.0)
    call_client_service(5)

    print("\n=== Phase 2: Trigger backend failures to open circuit ===")
    set_backend_failure(1.0, 500)
    call_client_service(5)

    print("\n=== Phase 3: Wait for half-open state (5s) ===")
    time.sleep(6)
    call_client_service(5)

    print("\n=== Phase 4: Restore backend (should close circuit) ===")
    set_backend_failure(0.0)
    time.sleep(6)
    call_client_service(10)

    print("\nExperiment complete! Check client logs for circuit transitions.")

if __name__ == "__main__":
    main()
