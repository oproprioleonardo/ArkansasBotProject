package com.leonardo.arkansasproject.executors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.ClassPath;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.leonardo.arkansasproject.Bot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@Singleton
public class LeadingExecutor extends ListenerAdapter {

    private final HashMap<CommandExecutor, Executor> executors = Maps.newHashMap();
    @Inject
    private Bot bot;

    @SuppressWarnings("UnstableApiUsage")
    public void run() {
        try {
            ClassPath.from(this.getClass().getClassLoader())
                    .getAllClasses()
                    .stream()
                    .filter(clazz -> clazz.getPackageName()
                            .equalsIgnoreCase("com.leonardo.arkansasproject.executors"))
                    .filter(clazz -> clazz.load().isAnnotationPresent(CommandExecutor.class))
                    .map(classInfo -> {
                        final Object instance = bot.getInjector().getInstance(classInfo.load());
                        Logger.getRootLogger().info("Executor " + instance.getClass().getSimpleName() + " registrado.");
                        return Maps.immutableEntry(classInfo.load().getAnnotation(CommandExecutor.class), (Executor) instance);
                    })
                    .forEach(entry -> executors.put(entry.getKey(), entry.getValue()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final Message message = event.getMessage();
        final String text = message.getContentRaw();
        final String[] args = text.split(" ");
        final String cmd = args[0];
        final Pattern patternCommand = Pattern.compile("&[A-Za-z]+");
        if (patternCommand.matcher(cmd).matches()) {
            final List<String> arguments = Lists.newArrayList(args);
            arguments.remove(0);
            this.executors.entrySet().stream()
                    .filter(entry -> Arrays.stream(entry.getKey().aliases()).anyMatch(s -> s.equalsIgnoreCase(cmd.substring(1))))
                    .forEach(entry -> entry.getValue().exec(event, event.getAuthor(), arguments.toArray(new String[]{})));
        }
    }
}
