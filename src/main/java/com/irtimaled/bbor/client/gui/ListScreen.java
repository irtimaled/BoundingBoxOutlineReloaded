package com.irtimaled.bbor.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public abstract class ListScreen extends GuiScreen {
    private final GuiScreen lastScreen;

    private AbstractButton doneButton;
    private String title;
    private ControlList controlList;
    private SearchField searchField;

    ListScreen(GuiScreen lastScreen) {
        this.lastScreen = lastScreen;
    }

    ListScreen() {
        this(null);
    }

    protected void onDoneClicked() {
        mc.displayGuiScreen(lastScreen);
    }

    @Override
    protected void initGui() {
        this.title = "Bounding Box Outline Reloaded";
        this.controlList = new ControlList(this.width, this.height, 48, this.height - 28);
        this.searchField = new SearchField(this.fontRenderer, this.width / 2 - 100, 22, 200, 20, this.controlList);
        this.doneButton = new AbstractButton(this.width / 2 - 100, this.height - 24, 200, I18n.format("gui.done")) {
            @Override
            public void onPressed() {
                onDoneClicked();
            }
        };

        this.children.add(this.doneButton);
        this.children.add(this.controlList);
        this.children.add(this.searchField);
        this.setup();
    }

    protected abstract void setup();

    @Override
    public void render(int mouseX, int mouseY, float unknown) {
        render(mouseX, mouseY);
    }

    protected void render(int mouseX, int mouseY) {
        this.controlList.render(mouseX, mouseY);

        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 8, 16777215);
        this.searchField.render(mouseX, mouseY);
        this.doneButton.render(mouseX, mouseY);
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
    public boolean mouseScrolled(double scrollAmount) {
        return this.controlList.mouseScrolled(scrollAmount);
    }

    @Override
    public void onGuiClosed() {
        this.controlList.close();
    }

    ControlList getControlList() {
        return this.controlList;
    }

    AbstractButton getDoneButton() {
        return doneButton;
    }
}
