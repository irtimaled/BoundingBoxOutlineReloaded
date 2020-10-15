package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.common.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("bbor")
public class ForgeMod {
    private static final CommonProxy proxy = DistExecutor.runForDist(() -> ForgeClientProxy::new, () -> CommonProxy::new);

    public ForgeMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(proxy);
        proxy.init();
    }
}
