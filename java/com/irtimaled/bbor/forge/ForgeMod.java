package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.ConfigManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = ForgeMod.MODID, name = ForgeMod.NAME, version = ForgeMod.VERSION, acceptedMinecraftVersions = ForgeMod.MCVERSION)
public class ForgeMod {

    public static final String MODID = "bbor";
    public static final String NAME = "Bounding Box Outline Reloaded";
    public static final String VERSION = "1.0.0-beta15";
    public static final String MCVERSION = "1.11.2";

    private ConfigManager configManager;

    public SimpleNetworkWrapper network;

    @Mod.Instance()
    public static ForgeMod instance;

    @SidedProxy(clientSide = "com.irtimaled.bbor.forge.ForgeClientProxy", serverSide = "com.irtimaled.bbor.forge.ForgeCommonProxy")
    public static ForgeCommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        configManager = new ConfigManager(evt.getModConfigurationDirectory());
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(proxy);

        proxy.init(configManager);
    }
}


