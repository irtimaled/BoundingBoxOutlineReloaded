package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.messages.PayloadBuilder;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;

import java.util.function.Consumer;

public class ServerPlayer {
    private final DimensionId dimensionId;
    private final Consumer<IPacket<?>> packetConsumer;

    public ServerPlayer(ServerPlayerEntity player) {
        this.dimensionId = DimensionId.from(player.getEntityWorld().getDimensionKey());
        this.packetConsumer = player.connection::sendPacket;
    }

    public DimensionId getDimensionId() {
        return dimensionId;
    }

    public void sendPacket(PayloadBuilder payloadBuilder) {
        packetConsumer.accept(payloadBuilder.build());
    }
}
