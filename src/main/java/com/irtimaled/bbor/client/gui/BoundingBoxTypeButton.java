package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.common.BoundingBoxType;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class BoundingBoxTypeButton extends BoolSettingButton {
    private final BoundingBoxType type;

    BoundingBoxTypeButton(int width, String label, BoundingBoxType type) {
        super(width, label, BoundingBoxTypeHelper.renderSetting(type));
        this.type = type;
    }

    @Override
    protected void renderBackground(MatrixStack matrixStack) {
        super.renderBackground(matrixStack);

        int left = getX() + 1;
        int top = getY() + 1;
        int right = left + width - 2;
        int bottom = top + height - 2;

        Color color = BoundingBoxTypeHelper.getColor(type);

        // top & left
        drawRectangle(matrixStack, left, top, right, top + 1, color);
        drawRectangle(matrixStack, left, top, left + 1, bottom, color);

        Color darker = color.darker();
        // bottom left & top right
        drawRectangle(matrixStack, left, bottom - 2, left + 1, bottom, darker);
        drawRectangle(matrixStack, right - 1, top, right, top + 1, darker);

        Color darkest = darker.darker();
        // bottom & right
        drawRectangle(matrixStack, left + 1, bottom - 2, right, bottom, darkest);
        drawRectangle(matrixStack, right - 1, top + 1, right, bottom, darkest);
    }
}
