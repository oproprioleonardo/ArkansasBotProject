package com.leonardo.arkansasproject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.leonardo.arkansasproject.di.ArkansasModule;
import com.leonardo.arkansasproject.dispatchers.Dispatcher;
import com.leonardo.arkansasproject.executors.LeadingExecutor;
import com.leonardo.arkansasproject.listeners.ButtonClickListener;
import com.leonardo.arkansasproject.listeners.MessageReceivedListener;
import com.leonardo.arkansasproject.managers.ReportProcessingManager;
import com.leonardo.arkansasproject.models.suppliers.ReportProcessing;
import com.leonardo.arkansasproject.services.ReportService;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.hibernate.reactive.mutiny.Mutiny;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

public class Bot {

    private static Bot instance;
    @Getter
    private final Injector injector;
    @Inject
    @Getter
    private CacheManager cacheManager;
    @Inject
    @Getter
    private ReportProcessingManager reportProcessingManager;
    @Getter
    @Inject
    private LeadingExecutor leadingExecutor;
    @Inject
    @Getter
    private Mutiny.SessionFactory sessionFactory;
    @Inject
    @Getter
    private ReportService reportService;
    @Inject
    @Getter
    private JDA jda;
    @Getter
    @Inject
    private Dispatcher dispatcher;
    @Getter
    private JsonObject config;

    public Bot() {
        this.createConfigurationFile();
        this.injector = Guice.createInjector(ArkansasModule.of(this));
        this.injector.injectMembers(this);
        this.reportProcessingManager.init();
        this.leadingExecutor.run();
        this.jda.addEventListener(this.leadingExecutor);
        this.jda.addEventListener(
                this.getInstance(MessageReceivedListener.class),
                this.getInstance(ButtonClickListener.class)
        );
        final CacheConfigurationBuilder<String, ReportProcessing> configurationBuilder = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, ReportProcessing.class, ResourcePoolsBuilder.heap(100));
        configurationBuilder.withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(120)));

    }

    public static Bot getInstance() {
        return instance == null ? instance = new Bot() : instance;
    }

    public <O> O getInstance(Class<O> clazz) {
        return this.injector.getInstance(clazz);
    }

    public void createConfigurationFile() {
        final File dataFolder = new File("botconfig/");
        if (!dataFolder.exists()) dataFolder.mkdirs();
        final File file = new File(dataFolder + "/config.json");
        try {
            if (!file.exists())
                Files.copy(this.getClass().getResourceAsStream("/config.json"), file.toPath());
            this.config = JsonParser.parseReader(new FileReader(file.getAbsolutePath())).getAsJsonObject();
        } catch (IOException ignored) {
        }
    }
}
