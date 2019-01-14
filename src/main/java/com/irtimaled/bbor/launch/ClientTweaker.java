package com.irtimaled.bbor.launch;

import java.io.File;
import java.util.List;

public class ClientTweaker extends Tweaker {
    @Override
    protected void addOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        addArg("--assetsDir", assetsDir.getPath());
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
