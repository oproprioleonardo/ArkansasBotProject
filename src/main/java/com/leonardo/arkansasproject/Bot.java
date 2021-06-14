package com.leonardo.arkansasproject;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.leonardo.arkansasproject.di.ArkansasModule;
import com.leonardo.arkansasproject.executors.LeadingExecutor;
import com.leonardo.arkansasproject.models.Report;
import com.leonardo.arkansasproject.models.suppliers.ReportProcessing;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.hibernate.reactive.mutiny.Mutiny;

import java.time.Duration;

public class Bot {

    public final Cache<String, ReportProcessing> REPORT_PROCESSING;
    @Getter
    private final CacheManager cacheManager;
    @Getter
    private Injector injector;
    @Getter
    @Inject
    private LeadingExecutor leadingExecutor;
    @Inject
    @Getter
    private Mutiny.SessionFactory sessionFactory;
    @Inject
    @Getter
    private JDA jda;

    public Bot() {
        this.injector = Guice.createInjector(ArkansasModule.of(this));
        this.cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
        this.injector.injectMembers(this);
        this.leadingExecutor.run();
        this.jda.addEventListener(this.leadingExecutor);
        this.cacheManager.init();
        final CacheConfigurationBuilder<String, ReportProcessing> configurationBuilder = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, ReportProcessing.class, ResourcePoolsBuilder.heap(100));
        configurationBuilder.withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(120)));
        this.REPORT_PROCESSING = this.cacheManager.createCache("REPORT_PROCESSING",
                configurationBuilder);

    }
}
