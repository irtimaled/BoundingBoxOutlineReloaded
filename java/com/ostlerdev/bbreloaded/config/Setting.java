package com.ostlerdev.bbreloaded.config;

public class Setting {

    private Object value;
    public String comment;

    public Setting(Object value) {

        this.value = value;
    }

    public Boolean getBoolean(Boolean defaultValue) {
        if (value instanceof Boolean)
            return (Boolean) value;

        return defaultValue;
    }

    public int getInt(int defaultValue) {
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

    public String getType() {
        if (value instanceof Integer)
            return "I";
        if (value instanceof Boolean)
            return "B";
        return "S";
    }

    public Object getValue() {
        return value;
    }
}