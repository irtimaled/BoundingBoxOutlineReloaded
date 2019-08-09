package com.irtimaled.bbor.mixin.network.play.server;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.messages.*;
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
        String channelName = channel.toString();
        if (channelName.startsWith("bbor:")) {
            PacketBuffer data = null;
            try {
                data = packet.getBufferData();
                PayloadReader reader = new PayloadReader(data);
                switch (channelName) {
                    case InitializeClient.NAME: {
                        EventBus.publish(InitializeClient.getEvent(reader));
                        ((NetHandlerPlayClient) netHandlerPlayClient).sendPacket(SubscribeToServer.getPayload().build());
                        break;
                    }
                    case AddBoundingBox.NAME: {
                        EventBus.publish(AddBoundingBox.getEvent(reader));
                        break;
                    }
                    case RemoveBoundingBox.NAME: {
                        EventBus.publish(RemoveBoundingBox.getEvent(reader));
                        break;
                    }
                }
            } finally {
                if (data != null)
                    data.release();
            }
        } else {
            netHandlerPlayClient.handleCustomPayload(packet);
        }
    }
}
