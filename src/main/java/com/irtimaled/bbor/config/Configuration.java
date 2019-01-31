package com.irtimaled.bbor.config;

import com.google.common.io.Files;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {
    private final File file;

    Configuration(File file) {
        this.file = file;
    }

    void save() {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "utf-8"));
            writer.write("# Configuration file\n");
            for (String category : settingsGroup.keySet()) {
                writer.write("\n");
                writer.write(String.format("%s {\n", category));
                Map<String, Setting> settings = settingsGroup.get(category);
                Boolean first = true;
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
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private Map<String, Map<String, Setting>> settingsGroup = new HashMap<>();

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
                    Object value = getTypedValue(type, stringValue);
                    Setting setting = new Setting(value);
                    setting.comment = lastCommentLine;
                    settingsGroup.get(category).put(name, setting);
                }
            }
        } catch (IOException ignored) {
        }
    }

    private Object getTypedValue(char type, String stringValue) {
        switch (type) {
            case 'I':
                return Integer.parseInt(stringValue);
            case 'B':
                return stringValue.equals("1") || stringValue.toLowerCase().equals("true");
        }
        return stringValue;
    }

    public Setting get(String category, String settingName, Object defaultValue) {
        if (!settingsGroup.containsKey(category)) {
            settingsGroup.put(category, new HashMap<>());
        }
        Map<String, Setting> settings = settingsGroup.get(category);
        if (!settings.containsKey(settingName)) {
            settings.put(settingName, new Setting(defaultValue));
        }
        return settings.get(settingName);
    }
}
