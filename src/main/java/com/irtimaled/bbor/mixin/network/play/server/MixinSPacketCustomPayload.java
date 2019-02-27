package com.irtimaled.bbor.mixin.network.play.server;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.messages.AddBoundingBox;
import com.irtimaled.bbor.common.messages.InitializeClient;
import com.irtimaled.bbor.common.messages.RemoveBoundingBox;
import com.irtimaled.bbor.common.messages.SubscribeToServer;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SPacketCustomPayload.class)
public abstract class MixinSPacketCustomPayload {
    @Shadow
    private ResourceLocation channel;

    @Redirect(method = "processPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/INetHandlerPlayClient;handleCustomPayload(Lnet/minecraft/network/play/server/SPacketCustomPayload;)V"))
    private void processPacket(INetHandlerPlayClient netHandlerPlayClient, SPacketCustomPayload packet) {
        PacketBuffer data = null;
        try {
            data = packet.getBufferData();
            if (InitializeClient.NAME.equals(channel)) {
                EventBus.publish(InitializeClient.getEvent(data));
                ((NetHandlerPlayClient) netHandlerPlayClient).sendPacket(SubscribeToServer.getPayload());
            } else if (AddBoundingBox.NAME.equals(channel)) {
                EventBus.publish(AddBoundingBox.getEvent(data));
            } else if (RemoveBoundingBox.NAME.equals(channel)) {
                EventBus.publish(RemoveBoundingBox.getEvent(data));
            } else {
                netHandlerPlayClient.handleCustomPayload(packet);
            }
        } finally {
            if (data != null)
                data.release();
        }
    }
}
