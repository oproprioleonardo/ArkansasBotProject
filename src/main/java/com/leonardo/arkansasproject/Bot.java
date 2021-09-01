package com.leonardo.arkansasproject;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.leonardo.arkansasproject.di.ArkansasModule;
import com.leonardo.arkansasproject.dispatchers.Dispatcher;
import com.leonardo.arkansasproject.executors.LeadingExecutor;
import com.leonardo.arkansasproject.listeners.ButtonClickListener;
import com.leonardo.arkansasproject.listeners.MessageReceivedListener;
import com.leonardo.arkansasproject.managers.ReportProcessingManager;
import com.leonardo.arkansasproject.services.ReportService;
import com.leonardo.arkansasproject.utils.BotConfig;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import org.apache.logging.log4j.LogManager;
import org.ehcache.CacheManager;
import org.hibernate.reactive.mutiny.Mutiny;

@Getter
public class Bot {

    private static Bot instance;
    @Getter
    private static Injector injector;
    @Inject
    private CacheManager cacheManager;
    @Inject
    private ReportProcessingManager reportProcessingManager;
    @Inject
    private LeadingExecutor leadingExecutor;
    @Inject
    private Mutiny.SessionFactory sessionFactory;
    @Inject
    private ReportService reportService;
    @Inject
    private JDA jda;
    @Inject
    private Dispatcher dispatcher;
    @Inject
    private BotConfig botConfig;
    @Inject
    private Dotenv dotenv;

    public Bot() {
        instance = this;
        try {
            injector = Guice.createInjector(ArkansasModule.of(this));
            injector.injectMembers(this);
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.getRootLogger().info("Configure seus dados em /botconfig/.env");
            System.exit(0);
            return;
        }
        this.reportProcessingManager.init(this.cacheManager);
        this.jda.addEventListener(this.leadingExecutor);
        this.jda.addEventListener(
                this.getInstance(MessageReceivedListener.class),
                this.getInstance(ButtonClickListener.class)
        );
        this.leadingExecutor.run();

    }

    public <O> O getInstance(Class<O> clazz) {
        return injector.getInstance(clazz);
    }

}
