package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.common.MathHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;

abstract class AbstractSlider extends AbstractControl {
    private final int optionCount;
    private final int total;
    int position = 0;

    AbstractSlider(int width, int optionCount) {
        super(0, 0, width, "");
        this.optionCount = optionCount;
        total = this.width - 8;
    }

    @Override
    protected void renderBackground(MatrixStack matrixStack) {
        this.minecraft.getTextureManager().bindTexture(WIDGETS_TEXTURE);
        int hoverState = this.isSelected() ? 1 : 0;
        drawTexture(matrixStack, this.getX() + (int) getProgressPercentage(), this.getY(), 0, 46 + hoverState * 20, 4, this.height);
        drawTexture(matrixStack, this.getX() + (int) getProgressPercentage() + 4, this.getY(), 196, 46 + hoverState * 20, 4, 20);
    }

    private double getProgressPercentage() {
        return (this.position / (double) this.optionCount) * (double) total;
    }

    private void changeProgress(double mouseX) {
        double progress = (mouseX - (double) (this.getX() + 4)) / (double) total;
        setPosition((int) Math.round(progress * optionCount));
    }

    protected int getPosition() {
        return position;
    }

    protected boolean setPosition(int position) {
        position = MathHelper.clamp(position, 0, optionCount);
        if (this.position == position) return false;

        this.position = position;
        onProgressChanged();
        return true;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        changeProgress(mouseX);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        changeProgress(mouseX);
    }

    protected abstract void onProgressChanged();

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key != 262 && key != 263) return false;
        int position = getPosition();
        return key == 263 ? setPosition(position - 1) : setPosition(position + 1);
    }

    @Override
    public void playDownSound(SoundManager soundHandler) {
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.playDownSound(MinecraftClient.getInstance().getSoundManager());
    }
}
