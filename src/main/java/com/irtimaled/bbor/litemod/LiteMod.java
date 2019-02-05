package com.irtimaled.bbor.litemod;

import com.irtimaled.bbor.client.BoundingBoxOutlineReloaded;
import com.mumfrey.liteloader.Tickable;
import net.minecraft.client.Minecraft;

import java.io.File;

public class LiteMod implements Tickable  {
    public LiteMod() {
    }

    @Override
    public String getName() {
        return "BoundingBoxOutlineReloaded";
    }

    @Override
    public String getVersion() {
        return "1.0.1";
    }

    @Override
    public void init(File configPath) {
        BoundingBoxOutlineReloaded.init();
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        BoundingBoxOutlineReloaded.keyPressed();
    }
}
