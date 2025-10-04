package com.adauction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PrivacyMetricsService {

    @Autowired
    private DifferentialPrivacyService privacyService;

    private final Map<String, Long> impressionCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> clickCounts = new ConcurrentHashMap<>();

    public void recordImpression(String adId) {
        impressionCounts.merge(adId, 1L, Long::sum);
    }

    public void recordClick(String adId) {
        clickCounts.merge(adId, 1L, Long::sum);
    }

    /**
     * Get privacy-preserving metrics
     * Adds differential privacy noise before returning
     */
    public Map<String, Object> getAdMetrics(String adId) {
        long trueImpressions = impressionCounts.getOrDefault(adId, 0L);
        long trueClicks = clickCounts.getOrDefault(adId, 0L);

        // Add noise with epsilon = 0.5 (stronger privacy)
        long noisyImpressions = privacyService.addCountNoise(trueImpressions, 0.5);
        long noisyClicks = privacyService.addCountNoise(trueClicks, 0.5);

        double ctr = noisyImpressions > 0
                ? (double) noisyClicks / noisyImpressions
                : 0.0;

        return Map.of(
                "adId", adId,
                "impressions", noisyImpressions,
                "clicks", noisyClicks,
                "ctr", ctr,
                "privacy_epsilon", 0.5
        );
    }
}