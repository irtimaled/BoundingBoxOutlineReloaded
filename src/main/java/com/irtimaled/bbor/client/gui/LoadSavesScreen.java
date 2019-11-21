package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSummary;

import java.util.List;

public class LoadSavesScreen extends ListScreen {
    public static void show() {
        ClientInterop.displayScreen(new LoadSavesScreen());
    }

    @Override
    protected void setup() {
        ControlList controlList = this.getControlList();
        controlList.showSelectionBox();
        try {
            final SaveFormat saveLoader = this.minecraft.getSaveLoader();
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
        this.getDoneButton().active = selectedEntry != null && selectedEntry.getVisible();
        super.render(mouseX, mouseY, unknown);
    }
}
