package com.irtimaled.bbor.config;

public abstract class SettingBase {
    String comment;
    String category;
    String name;

    private final char type;

    public SettingBase(char type) {
        this.type = type;
    }

    char getType() {
        return type;
    }

    abstract Object getValue();
}
