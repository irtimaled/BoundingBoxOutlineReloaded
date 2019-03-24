package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.messages.PayloadBuilder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;

import java.util.function.Consumer;

public class ServerPlayer {
    private final int dimensionId;
    private final Consumer<Packet<?>> packetConsumer;

    public ServerPlayer(EntityPlayerMP player) {
        this.dimensionId = player.dimension.getId();
        this.packetConsumer = player.connection::sendPacket;
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public void sendPacket(PayloadBuilder payloadBuilder) {
        packetConsumer.accept(payloadBuilder.build());
    }
}
