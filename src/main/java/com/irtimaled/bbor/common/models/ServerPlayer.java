package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.messages.PayloadBuilder;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.function.Consumer;

public class ServerPlayer {
    private final int dimensionId;
    private final Consumer<Packet<?>> packetConsumer;

    public ServerPlayer(ServerPlayerEntity player) {
        this.dimensionId = player.dimension.getRawId();
        this.packetConsumer = player.networkHandler::sendPacket;
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public void sendPacket(PayloadBuilder payloadBuilder) {
        packetConsumer.accept(payloadBuilder.build());
    }
}
