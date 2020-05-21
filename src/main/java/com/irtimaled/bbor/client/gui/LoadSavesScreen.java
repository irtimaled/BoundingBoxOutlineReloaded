package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSummary;

import java.util.List;

public class LoadSavesScreen extends ListScreen {
    private SelectableControlList controlList;

    public static void show() {
        ClientInterop.displayScreen(new LoadSavesScreen());
    }

    @Override
    protected ControlList buildList(int top, int bottom) {
        controlList = new SelectableControlList(this.width, this.height, top, bottom);
        try {
            final SaveFormat saveLoader = this.minecraft.getSaveLoader();
            List<WorldSummary> saveList = saveLoader.getSaveList();
            saveList.sort(null);
            saveList.forEach(world -> controlList.add(new WorldSaveRow(world, saveLoader, controlList::setSelectedEntry)));
        } catch (AnvilConverterException e) {
            e.printStackTrace();
        }
        return controlList;
    }

    @Override
    protected void onDoneClicked() {
        getSelectedEntry().done();
    }

    @Override
    public void render(int mouseX, int mouseY, float unknown) {
        ControlListEntry selectedEntry = getSelectedEntry();
        this.setCanExit(selectedEntry != null && selectedEntry.isVisible());
        super.render(mouseX, mouseY, unknown);
    }

    private ControlListEntry getSelectedEntry() {
        return this.controlList.getSelectedEntry();
    }
}
