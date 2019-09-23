package com.irtimaled.bbor.config;

public class Setting<T> extends AbstractSetting {
    private T value;

    Setting(char type, T value) {
        super(type);
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    @Override
    Object getValue() {
        return value;
    }
}
