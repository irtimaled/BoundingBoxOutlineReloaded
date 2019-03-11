package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.common.interop.CommonInterop;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("bbor")
public class ForgeMod {
    public static ForgeMod instance;

    public static ForgeCommonProxy proxy = DistExecutor.runForDist(() -> ForgeClientProxy::new, () -> ForgeCommonProxy::new);

    public ForgeMod() {
        instance = this;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        CommonInterop.init();
        MinecraftForge.EVENT_BUS.register(proxy);
        proxy.init();
    }
}
