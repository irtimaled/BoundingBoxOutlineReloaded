package com.irtimaled.bbor.client.gui;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class ControlListSection extends ControlListEntry {
    private static final int TITLE_HEIGHT = 16;
    private static final int CONTROLS_WIDTH = 310;
    private final String title;
    private final List<IControl> controls = new ArrayList<>();
    private final Minecraft minecraft = Minecraft.getInstance();
    private final int titleHeight;
    private int height;

    ControlListSection(String title, CreateControl... createControls) {
        this.title = title;
        this.titleHeight = title != null ? TITLE_HEIGHT : 0;
        this.height = titleHeight;

        int columnCount = columnCount();
        int controlWidth = (CONTROLS_WIDTH - ((columnCount - 1) * 4)) / columnCount;

        int column = 0;
        for (CreateControl createControl : createControls) {
            IControl control = createControl.create(0, controlWidth);
            if(control == null) continue;

            this.controls.add(control);
            if (column == 0) {
                this.height += control.getControlHeight();
            }
            column = (column + 1) % columnCount;
        }
    }

    private int columnCount() {
        switch (minecraft.getLanguageManager().getCurrentLanguage().getLanguageCode()){
            case "en_au":
            case "en_us":
            case "en_gb": return 3;
        }
        return 2;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        int x = this.getX();
        int y = this.getY();
        int top = y;
        if(this.title != null) {
            this.minecraft.fontRenderer.drawString(this.title, x + 4, y + ((TITLE_HEIGHT - this.minecraft.fontRenderer.FONT_HEIGHT) / 1.5f), 16777215);
            top += titleHeight;
        }

        int left = 0;
        int height = 0;
        for(IControl control : controls) {
            if(!control.getVisible()) continue;

            control.setX(left + x);
            control.setY(top);
            control.render(mouseX, mouseY);
            if(left == 0) {
                height = control.getControlHeight();
            }
            left += control.getControlWidth();
            if(left >= CONTROLS_WIDTH) {
                left = 0;
                top += height;
            }
        }
    }

    @Override
    public int getControlHeight() {
        return this.height;
    }

    @Override
    public int getControlWidth() {
        return CONTROLS_WIDTH;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (IControl control : controls) {
            if (control.getVisible() && control.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean result = false;
        for (IControl control : controls) {
            if (control.mouseReleased(mouseX, mouseY, button)) result = true;
        }
        return result;
    }

    @Override
    public void filter(String lowerValue) {
        if(matchesTitle(lowerValue)) lowerValue = "";

        int height = 0;
        int left = 0;
        for (IControl entry : controls) {
            entry.filter(lowerValue);
            if (entry.getVisible()) {
                if (left == 0)
                    height += entry.getControlHeight();
                left += entry.getControlWidth();
                if (left >= getControlWidth()) {
                    left = 0;
                }
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
}
