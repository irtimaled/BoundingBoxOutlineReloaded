package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.common.BoundingBoxType;

import java.awt.*;

public class BoundingBoxTypeButton extends BoolSettingButton {
    private final Color color;

    BoundingBoxTypeButton(int id, int x, int y, int width, String label, BoundingBoxType type) {
        super(id, x, y, width, label, type.shouldRenderSetting);
        color = type.getColor();
    }

    @Override
    protected void renderBackground() {
        int left = x + 1;
        int top = y + 1;
        int right = left + width - 2;
        int bottom = top + height - 2;

        // top & left
        drawRectangle(left, top, right, top + 1, color);
        drawRectangle(left, top, left + 1, bottom, color);

        Color darker = color.darker();
        // bottom left & top right
        drawRectangle(left, bottom - 2, left + 1, bottom, darker);
        drawRectangle(right - 1, top, right, top + 1, darker);

        Color darkest = darker.darker();
        // bottom & right
        drawRectangle(left + 1, bottom - 2, right, bottom, darkest);
        drawRectangle(right - 1, top + 1, right, bottom, darkest);
    }

    private void drawRectangle(int left, int top, int right, int bottom, Color color) {
        drawRect(left, top, right, bottom, color.getRGB());
    }
}
