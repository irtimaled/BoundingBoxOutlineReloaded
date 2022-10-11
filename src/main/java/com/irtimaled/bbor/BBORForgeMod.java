package com.irtimaled.bbor;

import com.irtimaled.bbor.common.CommonProxy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

@Mod("bbor")
@Mod.EventBusSubscriber
public class BBORForgeMod {
    @SubscribeEvent
    public static void onDedicatedSetup(FMLDedicatedServerSetupEvent event) {
        new CommonProxy().init();
    }
}
