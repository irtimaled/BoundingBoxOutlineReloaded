package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import com.irtimaled.bbor.bukkit.NMS.api.NMSMethodConsumer;
import com.irtimaled.bbor.common.messages.PayloadBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ServerPlayer {

    private final DimensionId dimensionId;
    private final NMSMethodConsumer packetConsumer;

    public ServerPlayer(Object player) {
        this.dimensionId = DimensionId.from(NMSHelper.worldGetResourceKey(NMSHelper.playerGetWorld(player)));
        this.packetConsumer = NMSHelper.playerGetPacketConsumer(player);
    }

    public DimensionId getDimensionId() {
        return dimensionId;
    }

    public void sendPacket(@NotNull PayloadBuilder payloadBuilder) {
        packetConsumer.accept(payloadBuilder.build());
    }
}
