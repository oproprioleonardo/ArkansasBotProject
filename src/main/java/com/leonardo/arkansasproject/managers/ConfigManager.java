package com.leonardo.arkansasproject.managers;

import com.google.common.collect.Sets;
import com.google.gson.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.leonardo.arkansasproject.Bot;
import com.leonardo.arkansasproject.models.Bug;
import com.leonardo.arkansasproject.models.BugCategory;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class ConfigManager {

    private final String path = "botconfig/config.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public Set<BugCategory> categories;
    @Getter
    private JsonObject config;

    @Inject
    private void providesBugCategories(Bot bot) {
        final File dataFolder = new File("botconfig/");
        if (!dataFolder.exists()) dataFolder.mkdirs();
        final File file = new File(dataFolder + "/config.json");
        try {
            if (!file.exists())
                Files.copy(this.getClass().getResourceAsStream("/config.json"), file.toPath());
            this.config = JsonParser.parseReader(new FileReader(file.getAbsolutePath())).getAsJsonObject();
        } catch (IOException ignored) {
        }
        this.categories = Sets.newHashSet();
        config.getAsJsonArray("categories").deepCopy()
              .forEach(jsonElement -> this.categories.add(gson.fromJson(jsonElement, BugCategory.class)));
    }


    public void editConfig() throws IOException {
        final FileWriter writer = new FileWriter(this.path);
        final String json = gson.toJson(categories);
        final JsonElement element = JsonParser.parseString(json);
        this.config.remove("categories");
        this.config.add("categories", element);
        writer.write(gson.toJson(this.config));
        writer.flush();
        writer.close();
    }

    public void addRoleAtBug(String id, String roleId) {
        this.categories.stream().map(BugCategory::getBugs).reduce((bugs, bugs2) -> {
            final HashSet<Bug> bugs1 = Sets.newHashSet(bugs);
            bugs1.addAll(bugs2);
            return bugs1;
        }).flatMap(bugs -> bugs.stream()
                               .filter(bug -> bug.getId().equalsIgnoreCase(id))
                               .findFirst())
                       .ifPresent(bug -> bug.getRoles().add(roleId));
    }

    public void removeRoleAtBug(String id, String roleId) {
        this.categories.stream().map(BugCategory::getBugs).reduce((bugs, bugs2) -> {
            final HashSet<Bug> bugs1 = Sets.newHashSet(bugs);
            bugs1.addAll(bugs2);
            return bugs1;
        }).flatMap(bugs -> bugs.stream()
                               .filter(bug -> bug.getId().equalsIgnoreCase(id))
                               .findFirst())
                       .ifPresent(bug -> bug.getRoles().removeIf(s -> s.equalsIgnoreCase(roleId)));
    }

}
