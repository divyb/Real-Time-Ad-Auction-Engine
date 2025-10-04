package com.adauction.service;

import com.adauction.model.*;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuctionService {

    @Autowired
    private BidFilterService filterService;

    @Autowired
    private UserProfileService userProfileService;

    /**
     * Second-price auction implementation
     * Winner pays the second-highest bid + $0.01
     */
    public AuctionResult runAuction(BidRequest request, List<Bid> allBids) {
        long startTime = System.nanoTime();

        // Step 1: Filter bids (targeting, floor price)
        List<Bid> eligibleBids = filterService.filterBids(request, allBids);

        // Step 2: Calculate effective CPM (bid * quality score)
        List<ScoredBid> scoredBids = eligibleBids.stream()
                .map(bid -> {
                    double effectiveCpm = bid.getBidPrice() * (bid.getQualityScore() / 100.0);
                    return new ScoredBid(bid, effectiveCpm);
                })
                .sorted(Comparator.comparingDouble(ScoredBid::getScore).reversed())
                .collect(Collectors.toList());

        // Step 3: Determine winner and second price
        AuctionResult result = new AuctionResult();
        result.setRequestId(request.getRequestId());
        result.setTotalBidsEvaluated(allBids.size());

        if (scoredBids.isEmpty()) {
            result.setProcessingTimeMs((System.nanoTime() - startTime) / 1_000_000);
            return result;  // No winner
        }

        ScoredBid winner = scoredBids.get(0);
        double secondPrice = scoredBids.size() > 1
                ? scoredBids.get(1).getScore() + 0.01
                : request.getFloorPrice();

        result.setWinningBidderId(winner.getBid().getBidderId());
        result.setAdId(winner.getBid().getAdId());
        result.setWinningBid(winner.getScore());
        result.setPricePaid(Math.max(secondPrice, request.getFloorPrice()));
        result.setCreativeUrl(winner.getBid().getCreativeUrl());
        result.setProcessingTimeMs((System.nanoTime() - startTime) / 1_000_000);

        return result;
    }

    @Data
    private static class ScoredBid {
        private final Bid bid;
        private final double score;
    }
}