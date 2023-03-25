package com.irtimaled.bbor.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;

abstract class AbstractControl extends PressableWidget implements IControl {
    private static final int PADDING = 4;
    protected final MinecraftClient minecraft;

    AbstractControl(int x, int y, int width, String name) {
        super(x, y, width, 20, Text.literal(name));
        this.minecraft = MinecraftClient.getInstance();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.render(matrixStack, mouseX, mouseY, 0f);
    }

    public int getControlHeight() {
        return this.height + PADDING;
    }

    public int getControlWidth() {
        return this.width + PADDING;
    }

    public void drawMessage(MatrixStack matrices, TextRenderer textRenderer, int color) {
        if (active) renderBackground(matrices);
        super.drawMessage(matrices, textRenderer, color);
    }

    protected void renderBackground(MatrixStack matrixStack) {
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    public void filter(String lowerValue) {
        String lowerString = this.getMessage().getString().toLowerCase();
        this.visible = lowerValue.equals("") ||
                lowerString.startsWith(lowerValue) ||
                lowerString.contains(" " + lowerValue);
    }

    void drawRectangle(MatrixStack matrixStack, int left, int top, int right, int bottom, Color color) {
        fill(matrixStack, left, top, right, bottom, color.getRGB());
    }

    @Override
    public void clearFocus() {
        this.setFocused(false);
    }

    @Override
    public void onPress() {
    }
}
