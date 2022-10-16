package com.irtimaled.bbor;

import com.irtimaled.bbor.common.CommonProxy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("bbor")
public class BBORForgeMod {

    public BBORForgeMod() {
        FMLJavaModLoadingContext.get().getModEventBus().register(new BBORForgeModListener());
    }

    public static class BBORForgeModListener {
        @SubscribeEvent
        public void onDedicatedSetup(FMLDedicatedServerSetupEvent event) {
            CommonProxy.isServer = true;
            new CommonProxy().init();
        }
    }
}
