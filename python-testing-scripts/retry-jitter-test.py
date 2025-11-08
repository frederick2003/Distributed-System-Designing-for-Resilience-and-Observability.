import requests, time, random, datetime

CLIENT_URL = "http://localhost:8081/api/test-retry"
BACKEND_CFG = "http://localhost:8080/config/failure"

# ---- Helper functions ----
def log(msg):
    now = datetime.datetime.now().strftime("%H:%M:%S.%f")[:-3]
    print(f"[{now}] {msg}")

def set_backend_failure(rate, code=500):
    """Configure backend failure rate dynamically."""
    try:
        r = requests.post(BACKEND_CFG, json={"failure_rate": rate, "status_code": code}, timeout=5)
        log(f"[Backend Config] failure_rate={rate}, status={code} -> {r.status_code}")
    except Exception as e:
        log(f"[Backend Config Error] {e}")

# ---- Retry demonstration ----
def call_with_retries(max_attempts=5, base_delay=2, multiplier=2, jitter=0.5):
    """Simulate exponential backoff with jitter."""
    for attempt in range(1, max_attempts + 1):
        try:
            r = requests.get(CLIENT_URL, timeout=3)
            if r.status_code == 200:
                log(f"Success on attempt {attempt}: {r.status_code}")
                return
            else:
                raise Exception(f"HTTP {r.status_code}")
        except Exception as e:
            log(f"Attempt {attempt} failed: {e}")
            if attempt == max_attempts:
                log("All retries failed. Giving up.")
                break

            # Compute exponential backoff delay with jitter
            delay = base_delay * (multiplier ** (attempt - 1))
            jitter_val = delay * jitter * (random.random() * 2 - 1)  # ± jitter%
            total_delay = round(delay + jitter_val, 2)
            log(f"Waiting {total_delay}s before next retry (base={delay}s ± jitter)")
            time.sleep(total_delay)

def main():
    log("=== RETRY + JITTER TEST ===")

    # Step 1: Force backend to fail (simulate transient outage)
    set_backend_failure(1.0, 500)
    log("Backend set to 100% failure rate")

    # Step 2: Call client with retries (will fail first few attempts)
    call_with_retries()

    # Step 3: Restore backend and retry again (should succeed)
    log("Restoring backend to healthy state (0% failure)")
    set_backend_failure(0.0, 500)
    call_with_retries()

    log("Experiment complete. Observe exponential backoff and jitter timing above.")

if __name__ == "__main__":
    main()
