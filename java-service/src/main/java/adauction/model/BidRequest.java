package com.adauction.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class BidRequest {
    private String requestId;
    private String userId;
    private String deviceType;  // mobile, desktop, tablet
    private List<String> interests;  // ["sports", "tech"]
    private Map<String, String> context;  // {"app": "news", "placement": "banner"}
    private double floorPrice;  // minimum bid price
    private long timestamp;
}