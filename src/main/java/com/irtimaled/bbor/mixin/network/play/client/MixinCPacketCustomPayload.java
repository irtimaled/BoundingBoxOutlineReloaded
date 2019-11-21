package com.irtimaled.bbor.mixin.network.play.client;

import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.messages.SubscribeToServer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CCustomPayloadPacket.class)
public class MixinCPacketCustomPayload {
    @Shadow
    private ResourceLocation channel;

    @Redirect(method = "processPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/IServerPlayNetHandler;processCustomPayload(Lnet/minecraft/network/play/client/CCustomPayloadPacket;)V"))
    private void processPacket(IServerPlayNetHandler netHandlerPlayServer, CCustomPayloadPacket packet) {
        if (this.channel.toString().equals(SubscribeToServer.NAME)) {
            CommonInterop.playerSubscribed(((ServerPlayNetHandler) netHandlerPlayServer).player);
        } else {
            netHandlerPlayServer.processCustomPayload(packet);
        }
    }
}
