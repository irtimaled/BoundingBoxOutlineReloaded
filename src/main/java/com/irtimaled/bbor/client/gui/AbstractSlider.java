package com.irtimaled.bbor.client.gui;

import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

abstract class AbstractSlider extends AbstractControl {
    double progress;
    private boolean isDragging;

    AbstractSlider(int x, int y, int width) {
        super(x, y, width, "");
    }

    @Override
    protected void renderBackground(int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bindTexture(BUTTON_TEXTURES);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if(isDragging) {
            changeProgress(mouseX);
        }

        int hoverState = super.getHoverState(this.hovered);
        this.drawTexturedModalRect(this.x + (int) (this.progress * (double) (this.width - 8)), this.y, 0, 46 + hoverState * 20, 4, this.height);
        this.drawTexturedModalRect(this.x + (int) (this.progress * (double) (this.width - 8)) + 4, this.y, 196, 46 + hoverState * 20, 4, 20);
    }

    boolean setProgress(double progress) {
        progress = MathHelper.clamp(progress, 0d, 1d);
        if (this.progress == progress) return false;

        this.progress = progress;
        return true;
    }

    private void changeProgress(double mouseX) {
        double progress = (mouseX - (double) (this.x + 4)) / (double) (this.width - 8);
        if (setProgress(progress)) {
            onProgressChanged();
        }
        updateText();
    }

    @Override
    protected int getHoverState(boolean hovered) {
        return 0;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        changeProgress(mouseX);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        changeProgress(mouseX);
        isDragging = true;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.isDragging = false;
    }

    protected abstract void updateText();

    protected abstract void onProgressChanged();
}
