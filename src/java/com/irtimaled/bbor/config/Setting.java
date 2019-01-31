package com.irtimaled.bbor.config;

public class Setting {
    private Object value;
    String comment;

    Setting(Object value) {
        this.value = value;
    }

    public Boolean getBoolean(Boolean defaultValue) {
        if (value instanceof Boolean)
            return (Boolean) value;

        return defaultValue;
    }

    int getInt(int defaultValue) {
        if (value instanceof Integer)
            return (Integer) value;

        return defaultValue;
    }

    public void set(Object value) {
        this.value = value;
    }

    public boolean getBoolean() {
        return getBoolean(false);
    }

    public int getInt() {
        return getInt(0);
    }

    String getType() {
        if (value instanceof Integer)
            return "I";
        if (value instanceof Boolean)
            return "B";
        return "S";
    }

    Object getValue() {
        return value;
    }
}
