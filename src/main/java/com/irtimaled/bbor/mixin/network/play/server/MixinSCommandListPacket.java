package com.irtimaled.bbor.mixin.network.play.server;

import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.common.TypeHelper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandTreeS2CPacket.class)
public class MixinSCommandListPacket {
    @Inject(method = "apply", at = @At("RETURN"))
    private void processPacket(ClientPlayPacketListener netHandlerPlayClient, CallbackInfo ci) {
        TypeHelper.doIfType(netHandlerPlayClient, ClientPlayNetworkHandler.class, handler ->
                ClientInterop.registerClientCommands(handler.getCommandDispatcher())
        );
    }
}
