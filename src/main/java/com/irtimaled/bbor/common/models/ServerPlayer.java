package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.messages.PayloadBuilder;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.Packet;

import java.util.function.Consumer;

public class ServerPlayer {
    private final DimensionId dimensionId;
    private final Consumer<Packet<?>> packetConsumer;

    public ServerPlayer(EntityPlayer player) {
        this.dimensionId = DimensionId.from(player.world.getDimensionKey());
        this.packetConsumer = player.playerConnection::sendPacket;
    }

    public DimensionId getDimensionId() {
        return dimensionId;
    }

    public void sendPacket(PayloadBuilder payloadBuilder) {
        packetConsumer.accept(payloadBuilder.build());
    }
}
