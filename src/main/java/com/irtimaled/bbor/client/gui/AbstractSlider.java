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
    protected void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bindTexture(WIDGETS_TEXTURE);
        int hoverState = super.getYImage(this.isHovered());
        this.drawTexture(matrixStack, this.x + (int) getProgressPercentage(), this.y, 0, 46 + hoverState * 20, 4, this.height);
        this.drawTexture(matrixStack, this.x + (int) getProgressPercentage() + 4, this.y, 196, 46 + hoverState * 20, 4, 20);
    }

    private double getProgressPercentage() {
        return (this.position / (double) this.optionCount) * (double) total;
    }

    private void changeProgress(double mouseX) {
        double progress = (mouseX - (double) (this.x + 4)) / (double) total;
        setPosition((int) (progress * optionCount));
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
    protected int getYImage(boolean hovered) {
        return 0;
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
