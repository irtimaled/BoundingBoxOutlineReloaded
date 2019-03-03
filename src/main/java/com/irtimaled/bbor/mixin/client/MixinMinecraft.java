package com.irtimaled.bbor.mixin.client;

import com.irtimaled.bbor.client.ClientProxy;
import com.irtimaled.bbor.client.events.KeyPressed;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        ConfigManager.loadConfig(((Minecraft) (Object) this).gameDir);
        new ClientProxy().init();
    }

    @Inject(method = "processKeyBinds", at = @At("HEAD"))
    public void processKeyBinds(CallbackInfo ci) {
        EventBus.publish(new KeyPressed());
    }
}
