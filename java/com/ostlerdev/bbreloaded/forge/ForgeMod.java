package com.ostlerdev.bbreloaded.forge;

import com.ostlerdev.bbreloaded.ConfigManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = ForgeMod.MODID, name = ForgeMod.NAME, version = ForgeMod.VERSION)
public class ForgeMod {

    public static final String MODID = "bbor";
    public static final String NAME = "Bounding Box Outline Reloaded";
    public static final String VERSION = "1.0.0-beta11";

    private ConfigManager configManager;

    public SimpleNetworkWrapper network;

    @Mod.Instance()
    public static ForgeMod instance;

    @SidedProxy(clientSide = "com.ostlerdev.bbreloaded.forge.ForgeClientProxy", serverSide = "com.ostlerdev.bbreloaded.forge.ForgeCommonProxy")
    public static ForgeCommonProxy proxy;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        configManager = new ConfigManager(evt.getModConfigurationDirectory());
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(proxy);
        FMLCommonHandler.instance().bus().register(proxy);

        proxy.init(configManager);
    }
}


