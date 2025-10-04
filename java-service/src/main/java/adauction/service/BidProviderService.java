package com.adauction.service;

import com.adauction.model.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class BidProviderService {

    private final Random random = new Random();

    /**
     * In production, this would send RTB requests to ad networks
     * For demo, we generate mock bids
     */
    public List<Bid> getBids(BidRequest request) {
        List<Bid> bids = new ArrayList<>();

        // Generate 20-50 mock bids
        int numBids = 20 + random.nextInt(31);

        for (int i = 0; i < numBids; i++) {
            Bid bid = new Bid();
            bid.setBidderId("bidder_" + i);
            bid.setAdId("ad_" + random.nextInt(1000));
            bid.setBidPrice(request.getFloorPrice() + random.nextDouble() * 5.0);
            bid.setQualityScore(50 + random.nextInt(51));  // 50-100
            bid.setCreativeUrl("https://ads.example.com/" + bid.getAdId());

            // Random targeting
            if (random.nextDouble() > 0.3) {
                bid.setTargetInterests(List.of(
                        request.getInterests().get(random.nextInt(request.getInterests().size()))
                ));
            }

            bids.add(bid);
        }

        return bids;
    }
}