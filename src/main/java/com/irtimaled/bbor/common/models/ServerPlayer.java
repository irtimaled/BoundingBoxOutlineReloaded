package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.messages.PayloadBuilder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;

import java.util.function.Consumer;

public class ServerPlayer {
    private final int dimensionId;
    private final Consumer<Packet<?>> packetConsumer;
    private final int playerId;

    public ServerPlayer(EntityPlayerMP player) {
        this.dimensionId = player.dimension;
        this.packetConsumer = player.connection::sendPacket;
        this.playerId = player.getEntityId();
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public void sendPacket(PayloadBuilder payloadBuilder) {
        packetConsumer.accept(payloadBuilder.build());
    }

    @Override
    public int hashCode() {
        return playerId;
    }
}
