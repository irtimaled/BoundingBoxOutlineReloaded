package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutCustomPayload;
import net.minecraft.resources.MinecraftKey;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PayloadBuilder {
    private static final Map<String, MinecraftKey> packetNames = new HashMap<>();

    static PayloadBuilder clientBound(String name) {
        return new PayloadBuilder(packetNames.computeIfAbsent(name, MinecraftKey::new), PacketPlayOutCustomPayload::new);
    }

    private final MinecraftKey name;
    private final BiFunction<MinecraftKey, PacketDataSerializer, Packet<?>> packetBuilder;
    private final PacketDataSerializer buffer;

    private PayloadBuilder(MinecraftKey name, BiFunction<MinecraftKey, PacketDataSerializer, Packet<?>> packetBuilder) {
        this.name = name;
        this.buffer = new PacketDataSerializer(Unpooled.buffer());
        this.packetBuilder = packetBuilder;
    }

    private Packet<?> packet;

    public Packet<?> build() {
        if (packet == null)
            packet = packetBuilder.apply(name, buffer);
        return packet;
    }

    PayloadBuilder writeLong(long value) {
        buffer.writeLong(value);
        packet = null;
        return this;
    }

    PayloadBuilder writeInt(int value) {
        buffer.writeInt(value);
        packet = null;
        return this;
    }

    PayloadBuilder writeVarInt(int value) {
        buffer.d(value);
        packet = null;
        return this;
    }

    PayloadBuilder writeChar(char value) {
        buffer.writeChar(value);
        packet = null;
        return this;
    }

    PayloadBuilder writeCoords(Coords coords) {
        return writeVarInt(coords.getX())
                .writeVarInt(coords.getY())
                .writeVarInt(coords.getZ());
    }

    public PayloadBuilder writeDimensionId(DimensionId dimensionId) {
        buffer.a(dimensionId.value());
        packet = null;
        return this;
    }
}
