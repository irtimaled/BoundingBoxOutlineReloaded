package com.irtimaled.bbor.launch;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class Tweaker implements ITweaker {
    private List<String> args;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.args = new ArrayList<>();
        addOptions(args, gameDir, assetsDir, profile);
    }

    void addArg(String name, String value) {
        if (value != null) {
            args.add(name);
            args.add(value);
        }
    }

    protected void addOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.args.addAll(args);
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.bbor.json");
        MixinEnvironment.getDefaultEnvironment().setSide(isClient() ? MixinEnvironment.Side.CLIENT : MixinEnvironment.Side.SERVER);
    }

    protected abstract boolean isClient();

    @Override
    public String[] getLaunchArguments() {
        return args.toArray(new String[0]);
    }
}
