package com.irtimaled.bbor.mixin.network.play.server;

import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.common.TypeHelper;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.play.server.SCommandListPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SCommandListPacket.class)
public class MixinSCommandListPacket {
    @Inject(method = "processPacket", at = @At("RETURN"))
    private void processPacket(IClientPlayNetHandler netHandlerPlayClient, CallbackInfo ci) {
        TypeHelper.doIfType(netHandlerPlayClient, ClientPlayNetHandler.class, handler ->
                ClientInterop.registerClientCommands(handler.func_195515_i()));
    }
}
