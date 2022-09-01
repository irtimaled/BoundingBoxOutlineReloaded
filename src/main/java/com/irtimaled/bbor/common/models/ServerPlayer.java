package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.messages.PayloadBuilder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.EntityPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ServerPlayer {

    private final DimensionId dimensionId;
    private final Consumer<Packet<?>> packetConsumer;

    public ServerPlayer(@NotNull EntityPlayer player) {
        this.dimensionId = DimensionId.from(player.s.aa());
        this.packetConsumer = player.b::a;
    }

    public DimensionId getDimensionId() {
        return dimensionId;
    }

    public void sendPacket(@NotNull PayloadBuilder payloadBuilder) {
        packetConsumer.accept(payloadBuilder.build());
    }
}
