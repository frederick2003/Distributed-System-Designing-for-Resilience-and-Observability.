import requests
import time

CLIENT_URL = "http://localhost:8081/api/test-circuit"
BACKEND_FAILURE_CONFIG_URL = "http://localhost:8080/config/failure"
BACKEND_LATENCY_CONFIG_URL = "http://localhost:8080/config/latency"


def set_backend_failure(failure_rate, error_code):
    """Configure backend failure behaviour."""
    data = {"failure_rate": failure_rate, "status_code": error_code}
    try:
        r = requests.post(BACKEND_FAILURE_CONFIG_URL, json=data)
        print(f"[Backend Config] {r.text}")
    except Exception as e:
        print(f"Failed to configure backend: {e}")

def call_client_service(times):
    """Call the client endpoint multiple times."""
    for i in range(times):
        try:
            r = requests.get(CLIENT_URL)
            print(f"Request {i+1:02d}: {r.text}")
        except Exception as e:
            print(f"Request {i+1:02d} failed: {e}")

def main():
    print("\n=== Phase 1: Normal operation (backend healthy) ===")
    ## Make a GET request to the backend service
    call_client_service(5)

    print("\n=== Phase 2: Trigger backend failures to open circuit ===")
    #  Send 5 HTTP 500 errror codes th
    set_backend_failure(1.0, 500)
    call_client_service(10)

    print("\n=== Phase 3: Observe HALF-OPEN after wait duration (5s) ===")
    print("Waiting 6 seconds for half-open state")
    time.sleep(6)
    call_client_service(5)
    print("\n=== Backend still failinging (HALF-OPEN => OPEN)")

    print("\n=== Phase 4: Restore backend (should close circuit) ===")

    print("\n Restore failure status to a 0% chance of failure")
    set_backend_failure(0.0, 500)

    print("\n Waiting 6 seconds before Half-Open test.")
    time.sleep(6)

    # Call client service with no failures
    call_client_service(10)
    # Should return the circuit back to closed.

    print("\nExperiment complete! Check client logs for circuit transitions.")

if __name__ == "__main__":
    main()
