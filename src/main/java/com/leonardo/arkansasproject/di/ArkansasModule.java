package com.leonardo.arkansasproject.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.leonardo.arkansasproject.Bot;
import com.leonardo.arkansasproject.dispatchers.Dispatcher;
import com.leonardo.arkansasproject.executors.CancelCmdExecutor;
import com.leonardo.arkansasproject.executors.ReportCmdExecutor;
import com.leonardo.arkansasproject.listeners.ExpireEventListener;
import com.leonardo.arkansasproject.listeners.MessageReceivedListener;
import com.leonardo.arkansasproject.repositories.ReportRepository;
import com.leonardo.arkansasproject.repositories.impl.ReportRepositoryImpl;
import com.leonardo.arkansasproject.services.ReportService;
import com.leonardo.arkansasproject.services.impl.ReportServiceImpl;
import com.leonardo.arkansasproject.utils.BotConfig;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.persistence.Persistence;

@AllArgsConstructor(staticName = "of")
public class ArkansasModule extends AbstractModule {

    private final Bot bot;

    protected void configure() {
        bind(Bot.class).toInstance(this.bot);
        bind(BotConfig.class);
        bind(ReportRepository.class).to(ReportRepositoryImpl.class);
        bind(ReportService.class).to(ReportServiceImpl.class);
        bind(Dispatcher.class);
        bind(ExpireEventListener.class);
        bind(ReportCmdExecutor.class);
        bind(MessageReceivedListener.class);
        bind(CancelCmdExecutor.class);
    }

    @Provides
    @Singleton
    public Mutiny.SessionFactory providesSessionFactory() {
        return Persistence.createEntityManagerFactory("mainProcessor").unwrap(Mutiny.SessionFactory.class);
    }

    @Provides
    @Singleton
    public CacheManager providesCacheManager() {
        final CacheManager manager = CacheManagerBuilder.newCacheManagerBuilder().build();
        manager.init();
        return manager;
    }

    @Provides
    @Singleton
    public Dotenv providesDotenv() {
        return Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().directory("./botconfig/").load();
    }

    @SneakyThrows
    @Provides
    @Singleton
    public JDA providesJDA(Dotenv dotenv) {
        final JDABuilder jdaBuilder = JDABuilder.createDefault(dotenv.get("AUTH_SECRET"));
        jdaBuilder.disableCache(CacheFlag.VOICE_STATE);
        jdaBuilder.setActivity(Activity.watching("Hylex Equipe"));
        jdaBuilder.setAutoReconnect(true);
        return jdaBuilder.build();
    }

}
