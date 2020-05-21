package com.irtimaled.bbor.client.config;

public class Setting<T> {
    private final char type;
    String comment;
    String category;
    String name;
    private T value;
    T defaultValue;

    Setting(char type, T value) {
        this.type = type;
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public void reset() {
        this.value = this.defaultValue;
    }

    public char getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
