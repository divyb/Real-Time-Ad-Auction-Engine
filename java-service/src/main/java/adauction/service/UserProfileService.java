package com.adauction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserProfileService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String USER_PROFILE_PREFIX = "user:profile:";

    /**
     * Get cached user profile with interests
     * Cache hit rate should be 95%+
     */
    public List<String> getUserInterests(String userId) {
        String key = USER_PROFILE_PREFIX + userId;

        @SuppressWarnings("unchecked")
        List<String> interests = (List<String>) redisTemplate.opsForValue().get(key);

        if (interests == null) {
            // Cache miss - fetch from "database" (mock)
            interests = fetchUserInterestsFromDb(userId);

            // Cache for 1 hour
            redisTemplate.opsForValue().set(key, interests, 1, TimeUnit.HOURS);
        }

        return interests;
    }

    private List<String> fetchUserInterestsFromDb(String userId) {
        // Mock database fetch
        Random random = new Random(userId.hashCode());
        String[] allInterests = {"sports", "tech", "news", "entertainment", "finance", "travel"};

        List<String> userInterests = new ArrayList<>();
        for (int i = 0; i < 2 + random.nextInt(3); i++) {
            userInterests.add(allInterests[random.nextInt(allInterests.length)]);
        }

        return userInterests;
    }

    public void warmupCache(List<String> userIds) {
        userIds.forEach(this::getUserInterests);
    }
}