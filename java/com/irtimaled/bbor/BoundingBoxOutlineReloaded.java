package com.irtimaled.bbor;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;


@Mod(modid = BoundingBoxOutlineReloaded.MODID, name = BoundingBoxOutlineReloaded.NAME, version = BoundingBoxOutlineReloaded.VERSION)
public class BoundingBoxOutlineReloaded {

    public static final String MODID = "bbor";
    public static final String NAME = "Bounding Box Outline Reloaded";
    public static final String VERSION = "1.0.0-beta7";

    private ConfigManager configManager;

    public SimpleNetworkWrapper network;

    @Mod.Instance()
    public static BoundingBoxOutlineReloaded instance;

    @SidedProxy(clientSide = "com.irtimaled.bbor.ClientProxy", serverSide = "com.irtimaled.bbor.CommonProxy")
    public static CommonProxy proxy;


    @EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        configManager = new ConfigManager(evt.getModConfigurationDirectory());
    }

    @EventHandler
    public void load(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(proxy);
        FMLCommonHandler.instance().bus().register(proxy);

        proxy.configManager = configManager;
        proxy.init();
    }
}

