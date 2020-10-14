package com.irtimaled.bbor.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;

public class SearchField extends TextFieldWidget implements IControl {
    private final ControlList controlList;

    SearchField(FontRenderer fontRenderer, int left, int top, int width, int height, ControlList controlList) {
        super(fontRenderer, left, top, width, height, new StringTextComponent(""));

        this.controlList = controlList;
        this.setResponder(text -> this.controlList.filter(removeLeadingSpaces(text.toLowerCase())));
        this.setTextFormatter((text, id) -> new StringTextComponent(removeLeadingSpaces(text)).func_241878_f());
        this.setFocused(true);
    }

    private String removeLeadingSpaces(String text) {
        return text.replaceFirst("^\\s++", "");
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.render(matrixStack, mouseX, mouseY, 0f);
    }

    @Override
    public boolean isVisible() {
        return super.getVisible();
    }
}
