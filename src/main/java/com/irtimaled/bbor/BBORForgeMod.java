package com.irtimaled.bbor;

import com.irtimaled.bbor.common.CommonProxy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.concurrent.atomic.AtomicBoolean;

@Mod("bbor")
public class BBORForgeMod {

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    public BBORForgeMod() {
        MinecraftForge.EVENT_BUS.register(new BBORForgeModListener());
    }

    public static class BBORForgeModListener {
        @SubscribeEvent
        public void onSetup(ServerAboutToStartEvent event) {
            if (FMLEnvironment.dist == Dist.DEDICATED_SERVER && initialized.compareAndSet(false, true)) {
                CommonProxy.isServer = true;
                new CommonProxy().init();
            }
        }
    }
}
