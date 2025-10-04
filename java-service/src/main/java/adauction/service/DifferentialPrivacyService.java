package com.adauction.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DifferentialPrivacyService {

    private static final double EPSILON = 1.0;  // Privacy budget
    private final Random random = new Random();

    /**
     * Add Laplace noise to user features for privacy
     * Laplace(μ, b) where b = sensitivity / epsilon
     */
    public Map<String, Double> addPrivacyNoise(Map<String, Double> userFeatures) {
        Map<String, Double> noisyFeatures = new HashMap<>();
        double sensitivity = 1.0;  // Assume features are normalized 0-1
        double scale = sensitivity / EPSILON;

        userFeatures.forEach((feature, value) -> {
            double noise = sampleLaplace(scale);
            double noisyValue = Math.max(0, Math.min(1, value + noise));
            noisyFeatures.put(feature, noisyValue);
        });

        return noisyFeatures;
    }

    /**
     * Sample from Laplace distribution
     * Laplace(0, b) = (1/2b) * exp(-|x|/b)
     */
    private double sampleLaplace(double scale) {
        double u = random.nextDouble() - 0.5;
        return -scale * Math.signum(u) * Math.log(1 - 2 * Math.abs(u));
    }

    /**
     * Add noise to aggregated metrics (impressions, clicks)
     */
    public long addCountNoise(long trueCount, double epsilon) {
        double scale = 1.0 / epsilon;
        long noise = (long) sampleLaplace(scale);
        return Math.max(0, trueCount + noise);
    }
}