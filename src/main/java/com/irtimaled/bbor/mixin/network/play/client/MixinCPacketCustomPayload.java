package com.irtimaled.bbor.mixin.network.play.client;

import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.messages.SubscribeToServer;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CustomPayloadC2SPacket.class)
public class MixinCPacketCustomPayload {
    @Shadow
    private Identifier channel;

    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/listener/ServerPlayPacketListener;onCustomPayload(Lnet/minecraft/network/packet/c2s/play/CustomPayloadC2SPacket;)V"))
    private void processPacket(ServerPlayPacketListener netHandlerPlayServer, CustomPayloadC2SPacket packet) {
        if (this.channel.toString().equals(SubscribeToServer.NAME)) {
            CommonInterop.playerSubscribed(((ServerPlayNetworkHandler) netHandlerPlayServer).player);
        } else {
            netHandlerPlayServer.onCustomPayload(packet);
        }
    }
}
