package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.messages.PayloadBuilder;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.function.Consumer;

public class ServerPlayer {
    private final DimensionId dimensionId;
    private final Consumer<Packet<?>> packetConsumer;

    public ServerPlayer(ServerPlayerEntity player) {
        this.dimensionId = DimensionId.from(player.getEntityWorld().getRegistryKey());
        this.packetConsumer = player.networkHandler::sendPacket;
    }

    public DimensionId getDimensionId() {
        return dimensionId;
    }

    public void sendPacket(PayloadBuilder payloadBuilder) {
        packetConsumer.accept(payloadBuilder.build());
    }
}
