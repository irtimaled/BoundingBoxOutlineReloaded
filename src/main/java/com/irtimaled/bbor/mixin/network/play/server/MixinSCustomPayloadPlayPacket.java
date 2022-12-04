package com.irtimaled.bbor.mixin.network.play.server;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.messages.AddBoundingBox;
import com.irtimaled.bbor.common.messages.InitializeClient;
import com.irtimaled.bbor.common.messages.PayloadReader;
import com.irtimaled.bbor.common.messages.StructureListSync;
import com.irtimaled.bbor.common.messages.SubscribeToServer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CustomPayloadS2CPacket.class)
public abstract class MixinSCustomPayloadPlayPacket {
    @Shadow
    private Identifier channel;

    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/listener/ClientPlayPacketListener;onCustomPayload(Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;)V"))
    private void processPacket(ClientPlayPacketListener netHandlerPlayClient, CustomPayloadS2CPacket packet) {
        String channelName = channel.toString();
        if (channelName.startsWith("bbor:")) {
            PacketByteBuf data = null;
            try {
                data = packet.getData();
                PayloadReader reader = new PayloadReader(data);
                switch (channelName) {
                    case InitializeClient.NAME -> {
                        EventBus.publish(InitializeClient.getEvent(reader));
                        ((ClientPlayNetworkHandler) netHandlerPlayClient).sendPacket(SubscribeToServer.getPayload().build());
                    }
                    case AddBoundingBox.NAME -> {
                        EventBus.publish(AddBoundingBox.getEvent(reader));
                    }
                    case StructureListSync.NAME -> {
                        StructureListSync.handleEvent(reader);
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
