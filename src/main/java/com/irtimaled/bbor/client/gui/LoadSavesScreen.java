package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;

import java.util.List;

public class LoadSavesScreen extends ListScreen {
    private SelectableControlList controlList;

    public static void show() {
        ClientInterop.displayScreen(new LoadSavesScreen(MinecraftClient.getInstance().currentScreen));
    }

    public LoadSavesScreen(Screen lastScreen) {
        super(lastScreen);
    }

    @Override
    protected ControlList buildList(int top, int bottom) {
        controlList = new SelectableControlList(this.width, this.height, top, bottom);
        try {
            final LevelStorage saveLoader = this.client.getLevelStorage();
            final List<LevelSummary> saveList = saveLoader.getLevelList();
            saveList.stream()
                    .sorted()
                    .forEach(world -> controlList.add(new WorldSaveRow(world, saveLoader, controlList::setSelectedEntry)));
        } catch (LevelStorageException e) {
            e.printStackTrace();
        }
        return controlList;
    }

    @Override
    protected void onDoneClicked() {
        getSelectedEntry().done();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float unknown) {
        ControlListEntry selectedEntry = getSelectedEntry();
        this.setCanExit(selectedEntry != null && selectedEntry.isVisible());
        super.render(matrixStack, mouseX, mouseY, unknown);
    }

    private ControlListEntry getSelectedEntry() {
        return this.controlList.getSelectedEntry();
    }
}
