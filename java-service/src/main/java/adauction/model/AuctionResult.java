package com.adauction.model;

import lombok.Data;

@Data
public class AuctionResult {
    private String requestId;
    private String winningBidderId;
    private String adId;
    private double winningBid;
    private double pricePaid;  // second-price
    private String creativeUrl;
    private long processingTimeMs;
    private int totalBidsEvaluated;
}