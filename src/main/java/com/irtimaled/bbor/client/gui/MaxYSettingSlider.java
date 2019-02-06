package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.config.Setting;

class MaxYSettingSlider extends IntSettingSlider {
    private final int actualMinValue;

    MaxYSettingSlider(int x, int y, int width, int minValue, Setting<Integer> setting) {
        super(x, y, width, minValue - 2, 127, "Max Y", setting);
        this.actualMinValue = minValue;
        this.setProgress(getSliderValue());
        this.addDisplayValue(-1, "Activated");
        this.addDisplayValue(0, "Player");
        this.addDisplayValue(63, "Sea Level");
    }

    @Override
    protected Integer getSettingValue() {
        Integer value = super.getSettingValue();
        if (value >= actualMinValue)
            return value;
        return (value + 1) - actualMinValue;
    }

    @Override
    protected double getSliderValue() {
        int value = setting.get();
        if (value < actualMinValue)
            value = (value - 1) + actualMinValue;

        return (value - minValue) / (double) range;
    }
}
