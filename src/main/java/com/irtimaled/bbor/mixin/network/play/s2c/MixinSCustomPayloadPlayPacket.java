package com.irtimaled.bbor.mixin.network.play.s2c;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.messages.*;
import com.irtimaled.bbor.common.messages.protocols.PacketSplitter;
import com.irtimaled.bbor.common.messages.servux.ServuxStructurePackets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CustomPayloadS2CPacket.class)
public abstract class MixinSCustomPayloadPlayPacket {
    @Shadow
    private Identifier channel;

    @Shadow @Final private PacketByteBuf data;

    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/listener/ClientPlayPacketListener;onCustomPayload(Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;)V"))
    private void processPacket(ClientPlayPacketListener netHandlerPlayClient, CustomPayloadS2CPacket packet) {
        String channelName = channel.toString();
        if (channelName.startsWith("bbor:")) {
            NetworkThreadUtils.forceMainThread(packet, netHandlerPlayClient, MinecraftClient.getInstance());
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
            return;
        } else if (channelName.equals("servux:structures")) {
            NetworkThreadUtils.forceMainThread(packet, netHandlerPlayClient, MinecraftClient.getInstance());

            PacketByteBuf data = null;
            try {
                data = PacketSplitter.receive(netHandlerPlayClient, packet);
                if (data != null) {
                    PayloadReader reader = new PayloadReader(data);
                    ServuxStructurePackets.handleEvent(reader);
                }
            } finally {
                if (data != null)
                    data.release();
            }
        }
        netHandlerPlayClient.onCustomPayload(packet);
    }
}
