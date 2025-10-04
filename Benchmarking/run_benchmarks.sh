#!/bin/bash

echo "=== Ad Auction Engine Benchmarks ==="
echo ""

# Start services
docker-compose up -d
sleep 10

# Warmup
echo "Warming up..."
ab -n 1000 -c 10 -p benchmark/sample_request.json -T application/json \
   http://localhost:8080/api/v1/auction > /dev/null 2>&1

echo ""
echo "=== Latency Test (1000 requests, concurrency=50) ==="
ab -n 1000 -c 50 -p benchmark/sample_request.json -T application/json \
   http://localhost:8080/api/v1/auction | grep -E "Requests per second|Time per request|50%|95%|99%"

echo ""
echo "=== Throughput Test (10000 requests, concurrency=100) ==="
ab -n 10000 -c 100 -p benchmark/sample_request.json -T application/json \
   http://localhost:8080/api/v1/auction | grep "Requests per second"

echo ""
echo "=== Running C++ benchmark ==="
cd cpp-service && ./bid_evaluator

echo ""
echo "Benchmarks complete!"