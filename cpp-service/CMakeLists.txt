#include <iostream>
#include <vector>
#include <algorithm>
#include <chrono>
#include <random>
#include <thread>
#include <mutex>

struct Bid {
    std::string bidder_id;
    std::string ad_id;
    double bid_price;
    int quality_score;
    std::vector<std::string> target_interests;
};

struct BidRequest {
    std::string request_id;
    std::string user_id;
    std::vector<std::string> interests;
    double floor_price;
};

class BidEvaluator {
private:
    std::mt19937 rng;

    // Simple thread pool for parallel evaluation
    static const int NUM_THREADS = 4;
    std::vector<std::thread> thread_pool;
    std::mutex result_mutex;

public:
    BidEvaluator() : rng(std::random_device{}()) {}

    // Fast bid filtering - returns eligible bids
    std::vector<Bid> filterBids(const BidRequest& request,
                                  const std::vector<Bid>& bids) {
        std::vector<Bid> eligible;
        eligible.reserve(bids.size());

        for (const auto& bid : bids) {
            if (bid.bid_price < request.floor_price) continue;

            // Check targeting match
            if (!bid.target_interests.empty()) {
                bool matches = false;
                for (const auto& interest : request.interests) {
                    if (std::find(bid.target_interests.begin(),
                                bid.target_interests.end(),
                                interest) != bid.target_interests.end()) {
                        matches = true;
                        break;
                    }
                }
                if (!matches) continue;
            }

            eligible.push_back(bid);
        }

        return eligible;
    }

    // Calculate effective CPM with quality score
    double calculateEffectiveCPM(const Bid& bid) {
        return bid.bid_price * (bid.quality_score / 100.0);
    }

    // Benchmark: Process 10K requests
    void benchmark() {
        const int NUM_REQUESTS = 10000;
        auto start = std::chrono::high_resolution_clock::now();

        for (int i = 0; i < NUM_REQUESTS; i++) {
            // Generate mock request
            BidRequest request;
            request.request_id = "req_" + std::to_string(i);
            request.user_id = "user_" + std::to_string(i % 1000);
            request.interests = {"sports", "tech"};
            request.floor_price = 1.0;

            // Generate 50 mock bids
            std::vector<Bid> bids;
            for (int j = 0; j < 50; j++) {
                Bid bid;
                bid.bidder_id = "bidder_" + std::to_string(j);
                bid.bid_price = 1.0 + (rng() % 500) / 100.0;
                bid.quality_score = 50 + (rng() % 51);
                bids.push_back(bid);
            }

            // Filter and evaluate
            auto eligible = filterBids(request, bids);

            double max_cpm = 0.0;
            for (const auto& bid : eligible) {
                double cpm = calculateEffectiveCPM(bid);
                max_cpm = std::max(max_cpm, cpm);
            }
        }

        auto end = std::chrono::high_resolution_clock::now();
        auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(end - start);

        std::cout << "Processed " << NUM_REQUESTS << " requests in "
                  << duration.count() << "ms" << std::endl;
        std::cout << "Average latency: "
                  << (double)duration.count() / NUM_REQUESTS << "ms" << std::endl;
        std::cout << "Throughput: "
                  << (NUM_REQUESTS * 1000.0) / duration.count() << " req/sec" << std::endl;
    }
};

int main() {
    std::cout << "=== C++ Bid Evaluator Benchmark ===" << std::endl;

    BidEvaluator evaluator;
    evaluator.benchmark();

    return 0;
}