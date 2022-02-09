package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.Versions;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.util.List;

public abstract class ListScreen extends Screen {
    private final Screen lastScreen;
    private static final String version = Versions.build;

    private ButtonWidget doneButton;
    private ControlList controlList;
    private SearchField searchField;

    ListScreen(Screen lastScreen) {
        super(new LiteralText("Bounding Box Outline Reloaded"));
        this.lastScreen = lastScreen;
    }

    ListScreen() {
        this(null);
    }

    protected void onDoneClicked() {
        ClientInterop.displayScreen(lastScreen);
    }

    @Override
    protected void init() {
        this.controlList = this.buildList(48, this.height - 28);
        this.searchField = new SearchField(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.controlList);
        this.doneButton = new ButtonWidget(this.width / 2 - 100, this.height - 24, 200, 20, new TranslatableText("gui.done"), buttonWidget -> onDoneClicked());

        this.addDrawableChild(this.searchField);
        ((List<Element>) this.children()).add(this.controlList);
        this.addDrawableChild(this.doneButton);
    }

    protected abstract ControlList buildList(int top, int bottom);

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float unknown) {
        render(matrixStack, mouseX, mouseY);
    }

    protected void render(MatrixStack matrixStack, int mouseX, int mouseY) {
        RenderSystem.assertOnRenderThread();
        this.renderBackground(matrixStack);
        this.controlList.render(matrixStack, mouseX, mouseY);

        this.drawCenteredText(matrixStack, this.textRenderer, this.title.asString(), this.width / 2, 8, 16777215);
        this.searchField.render(matrixStack, mouseX, mouseY);
        this.doneButton.render(matrixStack, mouseX, mouseY, 0f);

        int left = this.width - this.textRenderer.getWidth(version) - 2;
        int top = this.height - 10;
        this.drawStringWithShadow(matrixStack, this.textRenderer, version, left, top, -10658467);
    }

    @Override
    public void tick() {
        this.searchField.tick();
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        return super.keyPressed(key, scanCode, modifiers) || this.searchField.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        return this.searchField.charTyped(character, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        return this.controlList.mouseScrolled(mouseX, mouseY, scrollAmount);
    }

    @Override
    public void removed() {
        this.controlList.close();
    }

    protected void setCanExit(boolean canExit) {
        this.doneButton.active = canExit;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Element control : this.children()) {
            if (control.mouseClicked(mouseX, mouseY, button)) {
                Element focused = getFocused();
                if (focused instanceof IFocusableControl && focused != control) {
                    ((IFocusableControl) focused).clearFocus();
                }
                this.setFocused(control);
                if (button == 0) this.setDragging(true);
                return true;
            }
        }
        return false;
    }
}
