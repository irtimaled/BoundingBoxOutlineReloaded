package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.config.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;

class IntSettingSlider extends Button implements IRenderableControl {
    private final int maxValue;
    private final String prefix;
    private boolean dragging;
    private Map<Integer, String> displayValues = new HashMap<>();

    final Setting<Integer> setting;
    final int minValue;
    final int range;
    double sliderValue;

    IntSettingSlider(int id, int x, int y, int width, int minValue, int maxValue, String prefix, Setting<Integer> setting) {
        super(id, x, y, width, "");
        this.setting = setting;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.prefix = prefix;
        this.range = maxValue - minValue;
        this.sliderValue = getSliderValue();
        this.displayString = getDisplayValue();
    }

    IntSettingSlider addDisplayValue(int value, String displayValue) {
        displayValues.put(value, displayValue);
        if(setting.get() == value) {
            this.displayString = getDisplayValue();
        }
        return this;
    }

    private String getDisplayValue() {
        Integer value = setting.get();
        return prefix + ": " + displayValues.getOrDefault(value, value.toString());
    }

    protected Integer getSettingValue() {
        return MathHelper.clamp(minValue + (int) (range * sliderValue), minValue, maxValue);
    }

    protected double getSliderValue() {
        return MathHelper.clamp((setting.get() - minValue) / (double) range, 0d, 1d);
    }

    @Override
    protected int getHoverState(boolean p_getHoverState_1_) {
        return 0;
    }

    @Override
    protected void renderBg(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.dragging) {
            changeSlider(mouseX);
        }

        minecraft.getTextureManager().bindTexture(BUTTON_TEXTURES);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.x + (int) (this.sliderValue * (double) (this.width - 8)), this.y, 0, 66, 4, 20);
        this.drawTexturedModalRect(this.x + (int) (this.sliderValue * (double) (this.width - 8)) + 4, this.y, 196, 66, 4, 20);
    }

    public final void onClick(double mouseX, double mouseY) {
        changeSlider(mouseX);
        this.dragging = true;
    }

    private void changeSlider(double mouseX) {
        double proportion = (mouseX - (double) (this.x + 4)) / (double) (this.width - 8);
        this.sliderValue = MathHelper.clamp(proportion, 0d, 1d);
        this.setting.set(this.getSettingValue());
        this.displayString = this.getDisplayValue();
    }

    public void onRelease(double p_onRelease_1_, double p_onRelease_3_) {
        this.dragging = false;
    }
}
