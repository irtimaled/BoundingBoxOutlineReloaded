package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.common.interop.CommonInterop;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = ForgeMod.MODID, name = ForgeMod.NAME, version = ForgeMod.VERSION, acceptedMinecraftVersions = ForgeMod.MCVERSION, acceptableRemoteVersions = "*", guiFactory = "com.irtimaled.bbor.forge.ForgeConfigFactory")
public class ForgeMod {
    static final String MODID = "bbor";
    static final String NAME = "BoundingBoxOutlineReloaded";
    static final String VERSION = "@VERSION@";
    static final String MCVERSION = "@MC_VERSION@";

    @Mod.Instance()
    public static ForgeMod instance;

    @SidedProxy(clientSide = "com.irtimaled.bbor.forge.ForgeClientProxy", serverSide = "com.irtimaled.bbor.forge.ForgeCommonProxy")
    public static ForgeCommonProxy proxy;

    @Mod.EventHandler
    public void load(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(proxy);
        CommonInterop.init();
        proxy.init();
    }
}
