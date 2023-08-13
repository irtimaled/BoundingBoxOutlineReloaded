package com.irtimaled.bbor.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class ControlListSection extends ControlListEntry implements IControlSet {
    private static final int TITLE_HEIGHT = 16;
    private final String title;
    private final List<AbstractControl> controls = new ArrayList<>();
    private final MinecraftClient minecraft = MinecraftClient.getInstance();
    private final int titleHeight;
    private int height;
    private IControl focused;
    private boolean dragging;
    private boolean isFocused;

    ControlListSection(String title, int columnCount, CreateControl... createControls) {
        this.title = title;
        this.titleHeight = title != null ? TITLE_HEIGHT : 0;
        this.height = titleHeight;

        if (columnCount == -1) columnCount = defaultColumnCount();
        int controlWidth = (ControlList.CONTROLS_WIDTH - ((columnCount - 1) * 4)) / columnCount;

        int column = 0;
        for (CreateControl createControl : createControls) {
            AbstractControl control = createControl.create(controlWidth);
            if (control == null) continue;

            this.controls.add(control);
            if (column == 0) {
                this.height += control.getControlHeight();
            }
            column = (column + 1) % columnCount;
        }
    }

    private int defaultColumnCount() {
        switch (minecraft.getLanguageManager().getLanguage()) {
            case "en_au":
            case "en_us":
            case "en_gb":
                return 3;
        }
        return 2;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY) {
        int x = this.getX();
        int y = this.getY();
        int top = y;
        if (this.title != null) {
            if (ctx != null)
                ctx.drawText(this.minecraft.textRenderer, this.title, x + 4, (int) (y + ((TITLE_HEIGHT - this.minecraft.textRenderer.fontHeight) / 1.5f)), 16777215, false);
            top += titleHeight;
        }

        int left = 0;
        int height = 0;
        for (AbstractControl control : controls) {
            if (!control.isVisible()) continue;

            control.setX(left + x);
            control.setY(top);
            if (ctx != null)
                control.render(ctx, mouseX, mouseY);
            if (left == 0) {
                height = control.getControlHeight();
            }
            left += control.getControlWidth();
            if (left >= ControlList.CONTROLS_WIDTH) {
                left = 0;
                top += height;
            }
        }
    }

    @Override
    public void update() {
        super.update();
        this.render(null, 0, 0);
    }

    @Override
    public void clearFocus() {
        IControlSet.super.clearFocus();
    }

    @Override
    public int getControlHeight() {
        return this.height;
    }

    public int getControlWidth() {
        return ControlList.CONTROLS_WIDTH;
    }

    public void filter(String lowerValue) {
        if (matchesTitle(lowerValue)) lowerValue = "";

        int height = 0;
        int left = 0;
        for (AbstractControl entry : controls) {
            entry.filter(lowerValue);
            if (entry.isVisible()) {
                if (left == 0)
                    height += entry.getControlHeight();
                left += entry.getControlWidth();
                if (left >= getControlWidth()) {
                    left = 0;
                }
            } else if (entry == focused) {
                entry.clearFocus();
                focused = null;
            }
        }
        this.height = height + titleHeight;
        this.setVisible(height > 0);
    }

    private boolean matchesTitle(String lowerValue) {
        if (this.title == null) return false;

        String lowerString = this.title.toLowerCase();
        return lowerString.startsWith(lowerValue) ||
                lowerString.contains(" " + lowerValue);
    }

    @Override
    public List<? extends IControl> controls() {
        return this.controls;
    }

    @Override
    public IControl getFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(IControl focused) {
        this.focused = focused;
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Override
    public void setFocused(boolean focused) {
        this.isFocused = focused;
    }

    @Override
    public boolean isFocused() {
        return this.isFocused;
    }
}
