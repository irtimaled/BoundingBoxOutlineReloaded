package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.client.renderers.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEventHandler;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ControlList extends GuiEventHandler {
    private static final int CONTROLS_WIDTH = 310;
    private final int scrollBarLeft;
    private final int listHeight;
    private final int listLeft;
    private final int listRight;
    private final Minecraft minecraft;
    private final List<ControlListEntry> entries = new ArrayList<>();
    private final int width;
    private final int height;
    private final int top;
    private final int bottom;

    private int contentHeight = 8;
    private int selectedElement;
    private double amountScrolled;
    private boolean clickedScrollbar;
    private boolean showSelectionBox;
    private boolean transparentBackground;

    ControlList(int width, int height, int top, int bottom) {
        this.minecraft = Minecraft.getInstance();
        this.width = width;
        this.scrollBarLeft = width - 6;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.listHeight = bottom - top;
        this.listLeft = width / 2 - CONTROLS_WIDTH / 2;
        this.listRight = this.listLeft + CONTROLS_WIDTH;
        this.selectedElement = -1;
    }

    void add(ControlListEntry entry) {
        entry.list = this;
        entry.index = entries.size();
        addEntry(entry);
    }

    private void addEntry(ControlListEntry entry) {
        this.entries.add(entry);
        this.contentHeight += entry.getControlHeight();
    }

    ControlListEntry getSelectedEntry() {
        return this.selectedElement >= 0 && this.selectedElement < this.entries.size() ? this.entries.get(this.selectedElement) : null;
    }

    void filter(String lowerValue) {
        int height = 0;
        for (IControl entry : entries) {
            entry.filter(lowerValue);
            if (entry.getVisible())
                height += entry.getControlHeight();
        }
        this.contentHeight = height + 8;
    }

    void close() {
        this.entries.forEach(ControlListEntry::close);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.clickedScrollbar = button == 0 && mouseX >= (double) this.scrollBarLeft;
        if (mouseY >= (double) this.top && mouseY <= (double) this.bottom) {
            ControlListEntry entry = this.getEntryAt(mouseX, mouseY);
            if (entry != null && entry.mouseClicked(mouseX, mouseY, button)) {
                this.setDragging(true);
                this.setSelectedIndex(entry.index);
                return true;
            } else {
                return this.clickedScrollbar;
            }
        } else {
            return false;
        }
    }

    void setSelectedIndex(int index) {
        this.selectedElement = index;
    }

    private ControlListEntry getEntryAt(double mouseX, double mouseY) {
        if (mouseX >= listLeft && mouseX <= listRight) {
            for (ControlListEntry entry : entries) {
                if (!entry.getVisible()) continue;

                int top = entry.getY();
                int bottom = top + entry.getControlHeight();
                if (mouseY >= top && mouseY <= bottom)
                    return entry;
            }
        }
        return null;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        if (super.mouseDragged(mouseX, mouseY, button, p_mouseDragged_6_, p_mouseDragged_8_)) {
            return true;
        } else if (button == 0 && this.clickedScrollbar) {
            if (mouseY < (double) this.top) {
                this.amountScrolled = 0.0D;
            } else if (mouseY > (double) this.bottom) {
                this.amountScrolled = this.getMaxScroll();
            } else {
                double maxScroll = this.getMaxScroll();
                if (maxScroll < 1.0D) {
                    maxScroll = 1.0D;
                }

                double amountScrolled = maxScroll / (double) (this.listHeight - getScrollBarHeight());
                if (amountScrolled < 1.0D) {
                    amountScrolled = 1.0D;
                }

                this.amountScrolled += p_mouseDragged_8_ * amountScrolled;
            }

            return true;
        } else {
            return false;
        }
    }

    private int getMaxScroll() {
        return Math.max(0, this.contentHeight - (this.listHeight - 4));
    }

    private int getScrollBarHeight() {
        return MathHelper.clamp((int) ((float) (this.listHeight * this.listHeight) / (float) this.contentHeight),
                32,
                this.listHeight - 8);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.entries.forEach(entry -> entry.mouseReleased(mouseX, mouseY, button));
        return false;
    }

    @Override
    public boolean mouseScrolled(double scrollAmount) {
        this.amountScrolled -= scrollAmount * 10;
        return true;
    }

    public void render(int mouseX, int mouseY) {
        this.amountScrolled = MathHelper.clamp(this.amountScrolled, 0.0D, this.getMaxScroll());

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        if (!transparentBackground) drawListBackground();

        int listTop = this.top + 8 - (int) this.amountScrolled;

        drawEntries(mouseX, mouseY, listTop);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        this.overlayBackground(0, this.top);
        this.overlayBackground(this.bottom, this.height);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        drawOverlayShadows();

        int maxScroll = this.getMaxScroll();
        if (maxScroll > 0) {
            drawScrollBar(maxScroll);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void drawListBackground() {
        this.minecraft.getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);
        Renderer.startTextured()
                .setColor(32, 32, 32)
                .setAlpha(255)
                .addPoint(0, this.bottom, 0.0D, (float) 0 / 32.0F, (float) (this.bottom + (int) this.amountScrolled) / 32.0F)
                .addPoint(this.width, this.bottom, 0.0D, (float) this.width / 32.0F, (float) (this.bottom + (int) this.amountScrolled) / 32.0F)
                .addPoint(this.width, this.top, 0.0D, (float) this.width / 32.0F, (float) (this.top + (int) this.amountScrolled) / 32.0F)
                .addPoint(0, this.top, 0.0D, (float) 0 / 32.0F, (float) (this.top + (int) this.amountScrolled) / 32.0F)
                .render();
    }

    private void drawEntries(int mouseX, int mouseY, int top) {
        for (ControlListEntry entry : this.entries) {
            if (!entry.getVisible()) continue;

            entry.setX(this.listLeft);
            entry.setY(top);

            int height = entry.getControlHeight();

            if (this.showSelectionBox && this.selectedElement == entry.index) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                Renderer.startTextured()
                        .setAlpha(255)
                        .setColor(128, 128, 128)
                        .addPoint((double) this.listLeft - 2, (double) (top + height) - 2, 0.0D, 0.0D, 1.0D)
                        .addPoint((double) this.listRight + 2, (double) (top + height) - 2, 0.0D, 1.0D, 1.0D)
                        .addPoint((double) this.listRight + 2, top - 2, 0.0D, 1.0D, 0.0D)
                        .addPoint((double) this.listLeft - 2, top - 2, 0.0D, 0.0D, 0.0D)
                        .setColor(0, 0, 0)
                        .addPoint(this.listLeft - 1, (double) (top + height) - 3, 0.0D, 0.0D, 1.0D)
                        .addPoint(this.listRight + 1, (double) (top + height) - 3, 0.0D, 1.0D, 1.0D)
                        .addPoint(this.listRight + 1, top - 1, 0.0D, 1.0D, 0.0D)
                        .addPoint(this.listLeft - 1, top - 1, 0.0D, 0.0D, 0.0D)
                        .render();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            entry.render(mouseX, mouseY);
            top += entry.getControlHeight();
        }
    }

    private void overlayBackground(int top, int bottom) {
        this.minecraft.getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Renderer.startTextured()
                .setColor(64, 64, 64)
                .setAlpha(255)
                .addPoint(0, bottom, 0.0D, 0.0D, (float) bottom / 32.0F)
                .addPoint(this.width, bottom, 0.0D, (float) this.width / 32.0F, (float) bottom / 32.0F)
                .addPoint(this.width, top, 0.0D, (float) this.width / 32.0F, (float) top / 32.0F)
                .addPoint(0, top, 0.0D, 0.0D, (float) top / 32.0F)
                .render();
    }

    private void drawScrollBar(int maxScroll) {
        int scrollBarHeight = this.getScrollBarHeight();
        int scrollBarTop = (int) this.amountScrolled * (this.listHeight - scrollBarHeight) / maxScroll + this.top;
        if (scrollBarTop < this.top) {
            scrollBarTop = this.top;
        }

        Renderer.startTextured()
                .setAlpha(255)
                .addPoint(this.scrollBarLeft, this.bottom, 0.0D, 0.0D, 1.0D)
                .addPoint(this.width, this.bottom, 0.0D, 1.0D, 1.0D)
                .addPoint(this.width, this.top, 0.0D, 1.0D, 0.0D)
                .addPoint(this.scrollBarLeft, this.top, 0.0D, 0.0D, 0.0D)
                .render();

        Renderer.startTextured()
                .setColor(128, 128, 128)
                .setAlpha(255)
                .addPoint(this.scrollBarLeft, scrollBarTop + scrollBarHeight, 0.0D, 0.0D, 1.0D)
                .addPoint(this.width, scrollBarTop + scrollBarHeight, 0.0D, 1.0D, 1.0D)
                .addPoint(this.width, scrollBarTop, 0.0D, 1.0D, 0.0D)
                .addPoint(this.scrollBarLeft, scrollBarTop, 0.0D, 0.0D, 0.0D)
                .render();

        Renderer.startTextured()
                .setColor(192, 192, 192)
                .setAlpha(255)
                .addPoint(this.scrollBarLeft, scrollBarTop + scrollBarHeight - 1, 0.0D, 0.0D, 1.0D)
                .addPoint(this.width - 1, scrollBarTop + scrollBarHeight - 1, 0.0D, 1.0D, 1.0D)
                .addPoint(this.width - 1, scrollBarTop, 0.0D, 1.0D, 0.0D)
                .addPoint(this.scrollBarLeft, scrollBarTop, 0.0D, 0.0D, 0.0D)
                .render();
    }

    private void drawOverlayShadows() {
        Renderer.startTextured()
                .addPoint(0, this.top + 4, 0.0D, 0.0D, 1.0D)
                .addPoint(this.width, this.top + 4, 0.0D, 1.0D, 1.0D)
                .setAlpha(255)
                .addPoint(this.width, this.top, 0.0D, 1.0D, 0.0D)
                .addPoint(0, this.top, 0.0D, 0.0D, 0.0D)
                .render();
        Renderer.startTextured()
                .addPoint(this.width, this.bottom - 4, 0.0D, 1.0D, 0.0D)
                .addPoint(0, this.bottom - 4, 0.0D, 0.0D, 0.0D)
                .setAlpha(255)
                .addPoint(0, this.bottom, 0.0D, 0.0D, 1.0D)
                .addPoint(this.width, this.bottom, 0.0D, 1.0D, 1.0D)
                .render();
    }

    @Override
    protected List<? extends IGuiEventListener> getChildren() {
        return Collections.emptyList();
    }

    ControlList section(String title, CreateControl... createControls) {
        this.add(new ControlListSection(title, createControls));
        return this;
    }

    void showSelectionBox() {
        this.showSelectionBox = true;
    }

    void setTransparentBackground() {
        this.transparentBackground = true;
    }
}
