package com.irtimaled.bbor.client.gui;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;

import java.util.List;

public class LoadSavesScreen extends ListScreen {
    public static void show() {
        Minecraft.getInstance().displayGuiScreen(new LoadSavesScreen());
    }

    @Override
    protected void setup() {
        ControlList controlList = this.getControlList();
        controlList.showSelectionBox();
        try {
            final ISaveFormat saveLoader = this.mc.getSaveLoader();
            List<WorldSummary> saveList = saveLoader.getSaveList();
            saveList.sort(null);
            saveList.forEach(world -> controlList.add(new WorldSaveRow(world, saveLoader)));
        } catch (AnvilConverterException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDoneClicked() {
        ((WorldSaveRow) this.getControlList().getSelectedEntry()).loadWorld();
    }

    @Override
    public void render(int mouseX, int mouseY, float unknown) {
        ControlListEntry selectedEntry = this.getControlList().getSelectedEntry();
        this.getDoneButton().enabled = selectedEntry != null && selectedEntry.getVisible();
        super.render(mouseX, mouseY, unknown);
    }
}
