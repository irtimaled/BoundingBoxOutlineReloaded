package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.config.Setting;

import java.util.HashMap;
import java.util.Map;

class IntSettingSlider extends AbstractSlider implements IRenderableControl {
    private final String prefix;
    private Map<Integer, String> displayValues = new HashMap<>();

    final Setting<Integer> setting;
    final int minValue;
    final int range;

    IntSettingSlider(int x, int y, int width, int minValue, int maxValue, String prefix, Setting<Integer> setting) {
        super(x, y, width);
        this.setting = setting;
        this.minValue = minValue;
        this.prefix = prefix;
        this.range = maxValue - minValue;
        this.setProgress(getSliderValue());
        this.updateText();
    }

    IntSettingSlider addDisplayValue(int value, String displayValue) {
        displayValues.put(value, displayValue);
        if (setting.get() == value) {
            this.updateText();
        }
        return this;
    }

    private String getDisplayValue() {
        Integer value = setting.get();
        return prefix + ": " + displayValues.getOrDefault(value, value.toString());
    }

    protected Integer getSettingValue() {
        return minValue + (int) (range * progress);
    }

    protected double getSliderValue() {
        return (setting.get() - minValue) / (double) range;
    }

    @Override
    protected void updateText() {
        this.setMessage(this.getDisplayValue());
    }

    @Override
    protected void onProgressChanged() {
        this.setting.set(this.getSettingValue());
    }
}
