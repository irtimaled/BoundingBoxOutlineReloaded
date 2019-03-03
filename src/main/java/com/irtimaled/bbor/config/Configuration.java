package com.irtimaled.bbor.config;

import com.google.common.io.Files;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {
    public static final String FALLBACK_CATEGORY = "features";
    private final File file;

    Configuration(File file) {
        this.file = file;
    }

    void save() {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("# Configuration file\n");
            for (String category : settingsGroup.keySet()) {
                writer.write("\n");
                writer.write(String.format("%s {\n", category));
                Map<String, Setting<?>> settings = settingsGroup.get(category);
                boolean first = true;
                for (String settingName : settings.keySet()) {
                    if (!first)
                        writer.write("\n");
                    first = false;
                    Setting setting = settings.get(settingName);
                    writer.write(String.format("    # %s\n", setting.comment));
                    writer.write(String.format("    %s:%s=%s\n", setting.getType(), settingName, setting.getValue()));
                }
                writer.write("}\n");
            }
        } catch (IOException ignored) {
        }
    }

    private Map<String, Map<String, Setting<?>>> settingsGroup = new HashMap<>();

    void load() {
        try {
            List<String> lines = Files.readLines(file, Charset.forName("utf-8"));
            String category = null;
            String lastCommentLine = null;
            for (String line : lines) {
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty()) {
                    continue;
                }
                if (trimmedLine.startsWith("#")) {
                    lastCommentLine = trimmedLine.substring(1).trim();
                    continue;
                }
                if (trimmedLine.equals("}")) {
                    category = null;
                    continue;
                }
                if (category == null && trimmedLine.endsWith("{")) {
                    category = trimmedLine.substring(0, trimmedLine.length() - 1).trim();
                    settingsGroup.put(category, new HashMap<>());
                    continue;
                }
                if (category != null) {
                    String[] items = trimmedLine.split("[:=]");
                    char type = items[0].charAt(0);
                    String name = items[1];
                    String stringValue = items[2];
                    Setting setting = getTypedSetting(type, stringValue);
                    setting.comment = lastCommentLine;
                    settingsGroup.get(category).put(name, setting);
                }
            }
        } catch (IOException ignored) {
        }
    }

    private Setting<?> getTypedSetting(char type, String value) {
        switch (type) {
            case 'I':
                return new Setting<>(type, Integer.parseInt(value));
            case 'B':
                return new Setting<>(type, value.equals("1") || value.toLowerCase().equals("true"));
        }
        return new Setting<>(type, value);
    }

    <T> Setting<T> get(String category, String settingName, T defaultValue) {
        char type = getType(defaultValue);
        if (!settingsGroup.containsKey(category)) {
            settingsGroup.put(category, new HashMap<>());
        }
        Map<String, Setting<?>> settings = settingsGroup.get(category);
        Setting<?> setting = settings.get(settingName);
        if(setting == null && category != FALLBACK_CATEGORY)
            setting = getFallbackSetting(settingName, settings);
        if(setting != null && setting.getType() != type) {
            setting = null;
        }
        if (setting == null) {
            settings.put(settingName, setting = new Setting<>(type, defaultValue));
        }
        return (Setting<T>) setting;
    }

    private Setting<?> getFallbackSetting(String settingName, Map<String, Setting<?>> settings) {
        Map<String, Setting<?>> fallbackSettings = settingsGroup.get(FALLBACK_CATEGORY);
        if (fallbackSettings == null) return null;

        Setting<?> setting = fallbackSettings.get(settingName);
        if (setting != null) {
            fallbackSettings.remove(settingName);
            settings.put(settingName, setting);
        }
        return setting;
    }

    private <T> char getType(T defaultValue) {
        String[] typeNames = defaultValue.getClass().getName().split("[.]");
        return typeNames[typeNames.length-1].charAt(0);
    }

    void put(Setting<?> setting) {
        String category = setting.category;
        if (!settingsGroup.containsKey(category)) {
            settingsGroup.put(category, new HashMap<>());
        }
        Map<String, Setting<?>> settings = settingsGroup.get(category);
        settings.put(setting.name, setting);
    }
}
