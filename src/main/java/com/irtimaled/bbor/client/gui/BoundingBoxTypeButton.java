package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.common.BoundingBoxType;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class BoundingBoxTypeButton extends BoolSettingButton {
    private final Color color;

    BoundingBoxTypeButton(int id, int x, int y, int width, String label, BoundingBoxType type) {
        super(id, x, y, width, label, type.shouldRenderSetting);
        color = type.getColor();
    }

    @Override
    protected void renderBg(Minecraft p_renderBg_1_, int p_renderBg_2_, int p_renderBg_3_) {
        int left = x + 1;
        int top = y + 1;
        int right = left + width - 2;
        int bottom = top + height - 2;

        // top & left
        drawRect(left, top, right, top + 1, color.getRGB());
        drawRect(left, top, left + 1, bottom, color.getRGB());

        Color darker = color.darker();
        // bottom left & top right
        drawRect(left, bottom - 2, left + 1, bottom, darker.getRGB());
        drawRect(right - 1, top, right, top + 1, darker.getRGB());

        Color darkest = darker.darker();
        // bottom & right
        drawRect(left + 1, bottom - 2, right, bottom, darkest.getRGB());
        drawRect(right - 1, top + 1, right, bottom, darkest.getRGB());
    }
}
