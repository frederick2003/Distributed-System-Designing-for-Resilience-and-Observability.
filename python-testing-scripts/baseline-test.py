import requests
import time
from datetime import datetime

CLIENT_URL = "http://localhost:8080/api/data"       # ClientService endpoint
LATENCY_URL = "http://localhost:8080/config/latency" # BackendService latency config
FAILURE_URL = "http://localhost:8080/config/failure" # BackendService failure config

def log(message):
    """Helper function to log messages with timestamps."""
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    print(f"[{timestamp}] {message}")
    with open("baseline_results.log", "a") as f:
        f.write(f"[{timestamp}] {message}\n")

def reset_backend_config():
    """Reset latency and failure configurations."""
    try:
        requests.post(LATENCY_URL, json={"delay_ms": 0, "delay_rate": 0})
        requests.post(FAILURE_URL, json={"error_code": 200, "failure_rate": 0})
        log("Backend configuration reset to normal state.")
    except requests.exceptions.RequestException as e:
        log(f"Failed to reset backend config: {e}")

def perform_test(test_name, num_requests=5, delay_between=1):
    """Send a sequence of GET requests to the ClientService and measure latency."""
    log(f"=== {test_name} ===")
    for i in range(num_requests):
        start_time = time.time()
        try:
            response = requests.get(CLIENT_URL, timeout=5)
            elapsed = (time.time() - start_time) * 1000  # convert to milliseconds
            log(f"Request {i+1}: {response.status_code} | {elapsed:.2f} ms | {response.text.strip()}")
        except requests.exceptions.RequestException as e:
            elapsed = (time.time() - start_time) * 1000
            log(f"Request {i+1} failed after {elapsed:.2f} ms: {e}")
        time.sleep(delay_between)
    log(f"=== End of {test_name} ===\n")

def configure_latency(delay_ms, delay_rate):
    """Apply latency configuration."""
    try:
        response = requests.post(LATENCY_URL, json={"delay_ms": delay_ms, "delay_rate": delay_rate})
        log(f"Latency configured: {delay_ms}ms delay, rate {delay_rate} (Response {response.status_code})")
    except requests.exceptions.RequestException as e:
        log(f"Failed to configure latency: {e}")

def configure_failure(error_code, failure_rate):
    """Apply failure configuration."""
    try:
        response = requests.post(FAILURE_URL, json={"error_code": error_code, "failure_rate": failure_rate})
        log(f"Failure configured: HTTP {error_code}, rate {failure_rate} (Response {response.status_code})")
    except requests.exceptions.RequestException as e:
        log(f"Failed to configure failure: {e}")

def main():
    log("Starting baseline test sequence...\n")

    # Test 1: Normal operation
    reset_backend_config()
    perform_test("TEST 1: Normal Operation")

    # Test 2: Latency only
    configure_latency(delay_ms=2000, delay_rate=0.5)
    configure_failure(error_code=200, failure_rate=0)
    perform_test("TEST 2: Latency Introduced")

    # Test 3: Failure only
    configure_latency(delay_ms=0, delay_rate=0)
    configure_failure(error_code=500, failure_rate=0.5)
    perform_test("TEST 3: Failures Introduced")

    # Test 4: Combined latency and failures
    configure_latency(delay_ms=1500, delay_rate=0.5)
    configure_failure(error_code=500, failure_rate=0.5)
    perform_test("TEST 4: Latency and Failures Combined")

    # Reset backend state
    reset_backend_config()
    log("Baseline testing completed. All configurations reset.")

if __name__ == "__main__":
    main()
