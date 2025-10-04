from locust import HttpUser, task, between
import random
import json

class AuctionUser(HttpUser):
    wait_time = between(0.01, 0.05)  # 10-50ms between requests

    @task
    def run_auction(self):
        request_id = f"req_{random.randint(1, 1000000)}"
        user_id = f"user_{random.randint(1, 10000)}"

        payload = {
            "requestId": request_id,
            "userId": user_id,
            "deviceType": random.choice(["mobile", "desktop", "tablet"]),
            "interests": random.sample(
                ["sports", "tech", "news", "entertainment", "finance", "travel"],
                k=2
            ),
            "context": {
                "app": "news",
                "placement": "banner"
            },
            "floorPrice": 1.0,
            "timestamp": int(time.time() * 1000)
        }

        with self.client.post("/api/v1/auction",
                            json=payload,
                            catch_response=True) as response:
            if response.status_code == 200:
                result = response.json()
                if result.get("processingTimeMs", 999) > 100:
                    response.failure(f"Too slow: {result['processingTimeMs']}ms")
                else:
                    response.success()