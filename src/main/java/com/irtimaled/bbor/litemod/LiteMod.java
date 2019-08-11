package com.irtimaled.bbor.litemod;

import com.irtimaled.bbor.client.ClientProxy;
import com.irtimaled.bbor.common.interop.CommonInterop;

import java.io.File;

public class LiteMod implements com.mumfrey.liteloader.LiteMod {
    @Override
    public String getName() {
        return "BoundingBoxOutlineReloaded";
    }

    @Override
    public String getVersion() {
        return "@VERSION@";
    }

    @Override
    public void init(File configPath) {
        CommonInterop.init();
        new ClientProxy().init();
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {
    }
}
