package com.leonardo.arkansasproject.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.leonardo.arkansasproject.Bot;
import com.leonardo.arkansasproject.repositories.ReportRepository;
import com.leonardo.arkansasproject.repositories.ReportRepositoryImpl;
import com.leonardo.arkansasproject.services.ReportService;
import com.leonardo.arkansasproject.services.ReportServiceImpl;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.persistence.Persistence;

@AllArgsConstructor(staticName = "of")
public class ArkansasModule extends AbstractModule {

    private final Bot bot;

    protected void configure() {
        bind(Bot.class).toInstance(this.bot);
        bind(ReportRepository.class).to(ReportRepositoryImpl.class);
        bind(ReportService.class).to(ReportServiceImpl.class);
    }

    @Provides
    @Singleton
    public Mutiny.SessionFactory providesSessionFactory() {
        return Persistence.createEntityManagerFactory("mainProcessor").unwrap(Mutiny.SessionFactory.class);
    }

    @SneakyThrows
    @Provides
    @Singleton
    public JDA providesJDA() {
        final JDABuilder jdaBuilder = JDABuilder.createDefault(bot.getConfig().get("CLIENT_TOKEN").getAsString());
        jdaBuilder.disableCache(CacheFlag.VOICE_STATE);
        jdaBuilder.setActivity(Activity.watching("Control Server"));
        return jdaBuilder.build();
    }
}
