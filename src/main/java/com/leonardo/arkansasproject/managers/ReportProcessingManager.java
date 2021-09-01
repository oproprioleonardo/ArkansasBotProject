package com.leonardo.arkansasproject.managers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.leonardo.arkansasproject.listeners.ExpireEventListener;
import com.leonardo.arkansasproject.report.ReportProcessing;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheEventListenerConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.event.EventType;

import java.time.Duration;

@Singleton
public class ReportProcessingManager {

    public Cache<Long, ReportProcessing> REPORT_PROCESSING_CACHE;
    @Inject
    private ExpireEventListener eventListener;

    public void init(CacheManager cacheManager) {
        final CacheEventListenerConfigurationBuilder listener =
                CacheEventListenerConfigurationBuilder.newEventListenerConfiguration(eventListener, EventType.EXPIRED)
                                                      .unordered().synchronous();

        this.REPORT_PROCESSING_CACHE = cacheManager
                .createCache("REPORT_PROCESSING",
                             CacheConfigurationBuilder
                                     .newCacheConfigurationBuilder(
                                             Long.class,
                                             ReportProcessing.class,
                                             ResourcePoolsBuilder
                                                     .heap(30))
                                     .withExpiry(
                                             ExpiryPolicyBuilder
                                                     .timeToIdleExpiration(
                                                             Duration.ofSeconds(
                                                                     90)))
                                     .add(listener));


    }

    public boolean exists(Long userId) {
        return this.REPORT_PROCESSING_CACHE.containsKey(userId);
    }

    public ReportProcessing get(Long userId) {
        return this.REPORT_PROCESSING_CACHE.get(userId);
    }

    public void remove(Long userId) {
        this.REPORT_PROCESSING_CACHE.remove(userId);
    }

    public void put(Long userId, ReportProcessing rp) {
        this.REPORT_PROCESSING_CACHE.put(userId, rp);
    }


}
