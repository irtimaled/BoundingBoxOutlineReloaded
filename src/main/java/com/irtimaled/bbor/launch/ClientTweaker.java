package com.irtimaled.bbor.launch;

import java.io.File;
import java.util.List;

public class ClientTweaker extends Tweaker {
    @Override
    protected void addOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        if (!isOptifineLoaded()) {
            super.addOptions(args, gameDir, assetsDir, profile);
            addArg("--gameDir", gameDir != null ? gameDir.getAbsolutePath() : null);
            addArg("--assetsDir", assetsDir != null ? assetsDir.getPath() : null);
            addArg("--version", profile);
        }
    }

    private boolean isOptifineLoaded() {
        try {
            Class cls = Class.forName("optifine.OptiFineTweaker");
            return cls != null;
        } catch (Throwable e) {
            return false;
        }
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    protected boolean isClient() {
        return true;
    }
}
