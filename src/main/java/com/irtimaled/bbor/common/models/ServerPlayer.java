package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.messages.PayloadBuilder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.world.dimension.DimensionType;

import java.util.function.Consumer;

public class ServerPlayer {
    private final DimensionType dimensionType;
    private final Consumer<Packet<?>> packetConsumer;
    private final int playerId;

    public ServerPlayer(EntityPlayerMP player) {
        this.dimensionType = DimensionType.getById(player.dimension);
        this.packetConsumer = player.connection::sendPacket;
        this.playerId = player.getEntityId();
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    public void sendPacket(PayloadBuilder payloadBuilder) {
        packetConsumer.accept(payloadBuilder.build());
    }

    @Override
    public int hashCode() {
        return playerId;
    }
}
