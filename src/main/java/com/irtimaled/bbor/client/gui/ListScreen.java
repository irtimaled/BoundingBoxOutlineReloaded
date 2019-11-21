package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public abstract class ListScreen extends Screen {
    private final Screen lastScreen;

    private AbstractButton doneButton;
    private ControlList controlList;
    private SearchField searchField;

    ListScreen(Screen lastScreen) {
        super(new StringTextComponent("Bounding Box Outline Reloaded"));
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
        this.controlList = new ControlList(this.width, this.height, 48, this.height - 28);
        this.searchField = new SearchField(this.font, this.width / 2 - 100, 22, 200, 20, this.controlList);
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

        this.drawCenteredString(this.font, this.title.getUnformattedComponentText(), this.width / 2, 8, 16777215);
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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        return this.controlList.mouseScrolled(mouseX, mouseY, scrollAmount);
    }

    @Override
    public void removed() {
        this.controlList.close();
    }

    ControlList getControlList() {
        return this.controlList;
    }

    AbstractButton getDoneButton() {
        return doneButton;
    }
}
