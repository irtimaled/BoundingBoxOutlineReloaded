package com.irtimaled.bbor.mixin.network.play.client;

import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.messages.SubscribeToServer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CPacketCustomPayload.class)
public class MixinCPacketCustomPayload {
    @Shadow
    private String channel;

    @Redirect(method = "processPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/INetHandlerPlayServer;processCustomPayload(Lnet/minecraft/network/play/client/CPacketCustomPayload;)V"))
    private void processPacket(INetHandlerPlayServer netHandlerPlayServer, CPacketCustomPayload packet) {
        if (this.channel.equals(SubscribeToServer.NAME)) {
            CommonInterop.playerSubscribed(((NetHandlerPlayServer) netHandlerPlayServer).player);
        } else {
            netHandlerPlayServer.processCustomPayload(packet);
        }
    }
}
