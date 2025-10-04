package com.adauction.model;

import lombok.Data;

import java.util.List;

@Data
public class Bid {
    private String bidderId;
    private String adId;
    private double bidPrice;
    private int qualityScore;  // 1-100
    private String creativeUrl;
    private List<String> targetInterests;
}