package com.irtimaled.bbor.launch;

public class ServerTweaker extends Tweaker {
    @Override
    protected boolean isClient() {
        return false;
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.server.MinecraftServer";
    }
}
