package com.adauction.service;

import com.adauction.model.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BidFilterService {

    public List<Bid> filterBids(BidRequest request, List<Bid> bids) {
        return bids.stream()
                .filter(bid -> bid.getBidPrice() >= request.getFloorPrice())
                .filter(bid -> matchesTargeting(request, bid))
                .collect(Collectors.toList());
    }

    private boolean matchesTargeting(BidRequest request, Bid bid) {
        if (bid.getTargetInterests() == null || bid.getTargetInterests().isEmpty()) {
            return true;  // No targeting = show to everyone
        }

        // Check if any user interest matches bid targeting
        return request.getInterests().stream()
                .anyMatch(interest -> bid.getTargetInterests().contains(interest));
    }
}