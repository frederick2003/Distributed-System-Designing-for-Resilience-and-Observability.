import requests, time, threading, datetime

CLIENT_URL = "http://localhost:8081/api/test-retry"
BACKEND_CFG = "http://localhost:8080/config/failure"

def log(msg):
    now = datetime.datetime.now().strftime("%H:%M:%S.%f")[:-3]
    print(f"[{now}] {msg}")

def set_backend_failure(rate, code=500):
    try:
        r = requests.post(BACKEND_CFG, json={"failure_rate": rate, "status_code": code}, timeout=5)
        log(f"[Backend Config] Set failure_rate={rate}, status={code} -> {r.status_code}")
    except Exception as e:
        log(f"[Backend config] Error {e}")

def hit_client(thread_id):
    try:
        r = requests.get(CLIENT_URL, timeout=3)
        print(f"[{thread_id:02d}] {r.status_code} {r.text[:60]}")
    except Exception as e:
        print(f"[{thread_id:02d}] ERROR {e}")

def herd(concurrency):
    threads = [threading.Thread(target=hit_client, args=(i,)) for i in range(concurrency)]
    for t in threads: 
        t.start()
        time.sleep(0.05) # Small jitter
    for t in threads: 
        t.join()

def main():
    # Disable backend 
    set_backend_failure(1.0, 500)
    total_threads = 8
    fail_until_thread = 5
    time.sleep(1)

    threads = []

    for i in range(total_threads):
        thread = threading.Thread(target=hit_client, args=(i+1,))
        threads.append(thread)
        thread.start()
        time.sleep(0.2)

        if i + 1 == fail_until_thread:
            log("Restoring backend to healthy state")
            set_backend_failure(0.0,500)
    
    for thread in threads:
        thread.join()

    log("Experiment complete. Check client pod logs for retry delays & jitter.")

if __name__ == "__main__":
    main()