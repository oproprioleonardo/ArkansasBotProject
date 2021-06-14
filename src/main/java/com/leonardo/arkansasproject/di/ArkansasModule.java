package com.leonardo.arkansasproject.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.leonardo.arkansasproject.Bot;
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
        final JDABuilder jdaBuilder = JDABuilder.createDefault("ODM3Mzg3NDQ0NTIxMjcxMzI3.YIrz1A.8OFeE71RnhN0ji9GYnJal1zp9uE");
        jdaBuilder.disableCache(CacheFlag.VOICE_STATE);
        jdaBuilder.setActivity(Activity.watching("Control Server"));
        return jdaBuilder.build();
    }
}
