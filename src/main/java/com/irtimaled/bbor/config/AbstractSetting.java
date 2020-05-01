package com.irtimaled.bbor.config;

public abstract class AbstractSetting {
    String comment;
    String category;
    String name;

    private final char type;

    AbstractSetting(char type) {
        this.type = type;
    }

    public char getType() {
        return type;
    }

    public String getName() { return name; }

    abstract Object getValue();
}
