package com.irtimaled.bbor.mixin.network.play.server;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.messages.*;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CustomPayloadS2CPacket.class)
public abstract class MixinSPacketCustomPayload {
    @Shadow
    private Identifier channel;

    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/listener/ClientPlayPacketListener;onCustomPayload(Lnet/minecraft/client/network/packet/CustomPayloadS2CPacket;)V"))
    private void processPacket(ClientPlayPacketListener netHandlerPlayClient, CustomPayloadS2CPacket packet) {
        String channelName = channel.toString();
        if (channelName.startsWith("bbor:")) {
            PacketByteBuf data = null;
            try {
                data = packet.getData();
                PayloadReader reader = new PayloadReader(data);
                switch (channelName) {
                    case InitializeClient.NAME: {
                        EventBus.publish(InitializeClient.getEvent(reader));
                        ((ClientPlayNetworkHandler) netHandlerPlayClient).sendPacket(SubscribeToServer.getPayload().build());
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
            netHandlerPlayClient.onCustomPayload(packet);
        }
    }
}
