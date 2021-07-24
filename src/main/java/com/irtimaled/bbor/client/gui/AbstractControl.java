package com.irtimaled.bbor.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.awt.*;

abstract class AbstractControl extends ClickableWidget implements IControl {
    private static final int PADDING = 4;
    protected final MinecraftClient minecraft;

    AbstractControl(int x, int y, int width, String name) {
        super(x, y, width, 20, new LiteralText(name));
        this.minecraft = MinecraftClient.getInstance();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.render(matrixStack, mouseX, mouseY, 0f);
    }

    public void setX(int x) {
        super.x = x;
    }

    public void setY(int y) {
        super.y = y;
    }

    public int getControlHeight() {
        return this.height + PADDING;
    }

    public int getControlWidth() {
        return this.width + PADDING;
    }

    @Override
    protected void renderBackground(MatrixStack matrixStack, MinecraftClient minecraft, int mouseX, int mouseY) {
        if (active) renderBackground(matrixStack, mouseX, mouseY);
    }

    protected void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY) {
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    public void filter(String lowerValue) {
        String lowerString = this.getMessage().asString().toLowerCase();
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
}
