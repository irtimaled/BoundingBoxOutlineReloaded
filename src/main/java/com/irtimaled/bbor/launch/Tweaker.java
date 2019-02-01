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
    public List<String> args;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.args = new ArrayList<>(args);
        addArg("--version", profile);
        addOptions(args, gameDir, assetsDir, profile);
    }

    protected void addArg(String name, String value) {
        args.add(name);
        if (value != null) {
            args.add(value);
        }
    }

    protected void addOptions(List<String> args, File gameDir, File assetsDir, String profile) {
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        // Use the Launch classLoader to load the RiftLoader class. Otherwise identical
        // classes may not be equal, and 'instanceof' may return false when it should be true.
        /*try {
            Class<?> clazz = Launch.classLoader.findClass("org.dimdev.riftloader.RiftLoader");
            clazz.getMethod("load", boolean.class).invoke(clazz.getField("instance").get(null), isClient());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
*/
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
