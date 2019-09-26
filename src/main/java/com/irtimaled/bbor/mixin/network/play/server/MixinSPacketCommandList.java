package com.irtimaled.bbor.mixin.network.play.server;

import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.common.TypeHelper;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketCommandList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SPacketCommandList.class)
public class MixinSPacketCommandList {
    @Inject(method = "processPacket", at= @At("RETURN"))
    private void processPacket(INetHandlerPlayClient netHandlerPlayClient, CallbackInfo ci) {
        TypeHelper.doIfType(netHandlerPlayClient, NetHandlerPlayClient.class, handler -> {
            ClientInterop.registerClientCommands(handler.func_195515_i());
        });
    }
}
