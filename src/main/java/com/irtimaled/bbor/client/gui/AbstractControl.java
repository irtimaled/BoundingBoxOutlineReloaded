package com.irtimaled.bbor.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.PressableWidget;
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
    public void render(DrawContext ctx, int mouseX, int mouseY) {
        super.render(ctx, mouseX, mouseY, 0f);
    }

    public int getControlHeight() {
        return this.height + PADDING;
    }

    public int getControlWidth() {
        return this.width + PADDING;
    }

    public void drawMessage(DrawContext ctx, TextRenderer textRenderer, int color) {
        if (active) renderBackground(ctx);
        super.drawMessage(ctx, textRenderer, color);
    }

    protected void renderBackground(DrawContext ctx) {
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

    void drawRectangle(DrawContext ctx, int left, int top, int right, int bottom, Color color) {
        ctx.fill(left, top, right, bottom, color.getRGB());
    }

    @Override
    public void clearFocus() {
        this.setFocused(false);
    }

    @Override
    public void onPress() {
    }
}
