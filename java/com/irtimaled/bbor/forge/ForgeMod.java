package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.config.ConfigManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = ForgeMod.MODID, name = ForgeMod.NAME, version = ForgeMod.VERSION, acceptedMinecraftVersions = ForgeMod.MCVERSION, acceptableRemoteVersions = "*")
public class ForgeMod {
    static final String MODID = "bbor";
    static final String NAME = "BoundingBoxOutlineReloaded";
    static final String VERSION = "1.0.3";
    static final String MCVERSION = "1.12.2";

    public SimpleNetworkWrapper network;

    @Mod.Instance()
    public static ForgeMod instance;

    @SidedProxy(clientSide = "com.irtimaled.bbor.forge.ForgeClientProxy", serverSide = "com.irtimaled.bbor.forge.ForgeCommonProxy")
    public static ForgeCommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        ConfigManager.loadConfig(evt.getModConfigurationDirectory());
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(proxy);
        proxy.init();
    }
}
