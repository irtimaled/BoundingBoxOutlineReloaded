package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.client.renderers.RenderHelper;
import com.irtimaled.bbor.client.renderers.Renderer;
import net.minecraft.client.util.math.MatrixStack;

public class SelectableControlList extends ControlList {
    private final int listRight;

    private int selectedElement;
    private boolean isFocused;

    SelectableControlList(int width, int height, int top, int bottom) {
        super(width, height, top, bottom);
        this.listRight = this.listLeft + CONTROLS_WIDTH;
        this.selectedElement = -1;
    }

    @Override
    public void filter(String lowerValue) {
        super.filter(lowerValue);
        if (selectedElement >= 0) {
            if (selectNextVisibleElement(true, selectedElement) ||
                    selectNextVisibleElement(true, 0)) return;
            selectedElement = -1;
        }
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key != 264 && key != 265 && key != 257) return false;

        if (key == 257) {
            if (selectedElement >= 0) {
                getSelectedEntry().done();
                return true;
            }
            return false;
        }

        boolean moveForward = key == 264;
        if (selectedElement >= 0) {
            int newIndex = selectedElement + (moveForward ? 1 : 0);
            if (selectNextVisibleElement(moveForward, newIndex)) return true;
        }
        if (selectNextVisibleElement(moveForward, moveForward ? 0 : entries.size())) return true;

        this.selectedElement = -1;
        return false;
    }

    private boolean selectNextVisibleElement(boolean moveForward, int index) {
        return ListHelper.findNextMatch(entries, index, moveForward, ControlListEntry::isVisible,
                entry -> this.selectedElement = entry.index);
    }

    ControlListEntry getSelectedEntry() {
        return this.selectedElement >= 0 && this.selectedElement < this.entries.size() ?
                this.entries.get(this.selectedElement) :
                null;
    }

    void setSelectedEntry(ControlListEntry entry) {
        if (entry != null) {
            this.selectedElement = entry.index;
        } else {
            this.selectedElement = -1;
        }
    }

    @Override
    public boolean changeFocus(boolean moveForward) {
        if (contentHeight == PADDING) return false;

        isFocused = !isFocused;
        if (getSelectedEntry() == null && this.entries.size() > 0) {
            setSelectedEntry(this.entries.get(0));
        }
        return isFocused;
    }

    @Override
    protected void drawEntry(MatrixStack matrixStack, int mouseX, int mouseY, int top, ControlListEntry entry, int height) {
        if (this.selectedElement == entry.index) {
            RenderHelper.disableTexture();
            int color = this.isFocused ? 255 : 128;
            Renderer.startQuads()
                    .setMatrixStack(matrixStack)
                    .setAlpha(255)
                    .setColor(color, color, color)
                    .addPoint((double) this.listLeft - 2, (double) (top + height) - 2, 0.0D)
                    .addPoint((double) this.listRight + 2, (double) (top + height) - 2, 0.0D)
                    .addPoint((double) this.listRight + 2, top - 2, 0.0D)
                    .addPoint((double) this.listLeft - 2, top - 2, 0.0D)
                    .setColor(0, 0, 0)
                    .addPoint(this.listLeft - 1, (double) (top + height) - 3, 0.0D)
                    .addPoint(this.listRight + 1, (double) (top + height) - 3, 0.0D)
                    .addPoint(this.listRight + 1, top - 1, 0.0D)
                    .addPoint(this.listLeft - 1, top - 1, 0.0D)
                    .render();
            RenderHelper.enableTexture();
        }
        super.drawEntry(matrixStack, mouseX, mouseY, top, entry, height);
    }

    @Override
    public void clearFocus() {
        this.isFocused = false;
    }
}
