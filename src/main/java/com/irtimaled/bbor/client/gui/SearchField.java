package com.irtimaled.bbor.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class SearchField extends GuiTextField {
    private final ControlList controlList;

    SearchField(FontRenderer fontRenderer, int left, int top, int width, int height, ControlList controlList) {
        super(0, fontRenderer, left, top, width, height);

        this.controlList = controlList;
        this.setTextAcceptHandler((id, text) -> this.controlList.filter(removeLeadingSpaces(text.toLowerCase())));
        this.setTextFormatter((text, id) -> removeLeadingSpaces(text));
        this.setFocused(true);
        this.setCanLoseFocus(false);
    }

    private String removeLeadingSpaces(String text) {
        return text.replaceFirst("^\\s++", "");
    }

    public void render(int mouseX, int mouseY) {
        this.drawTextField(mouseX, mouseY, 0f);
    }

    @Override
    public void setFocused(boolean ignored) {
        super.setFocused(true);
    }
}
