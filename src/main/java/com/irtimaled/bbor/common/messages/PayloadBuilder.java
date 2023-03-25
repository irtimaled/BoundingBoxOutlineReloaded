package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class PayloadBuilder {
    private static final Map<String, Identifier> packetNames = new ConcurrentHashMap<>();

    public static PayloadBuilder clientBound(String name) {
        return new PayloadBuilder(packetNames.computeIfAbsent(name, Identifier::new), CustomPayloadS2CPacket::new);
    }

    public static PayloadBuilder serverBound(String name) {
        return new PayloadBuilder(packetNames.computeIfAbsent(name, Identifier::new), CustomPayloadC2SPacket::new);
    }

    private final Identifier name;
    private final BiFunction<Identifier, PacketByteBuf, Packet<?>> packetBuilder;
    private final PacketByteBuf buffer;

    private PayloadBuilder(Identifier name, BiFunction<Identifier, PacketByteBuf, Packet<?>> packetBuilder) {
        this.name = name;
        this.buffer = new PacketByteBuf(Unpooled.buffer());
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
        buffer.writeVarInt(value);
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
        buffer.writeIdentifier(dimensionId.getValue());
        packet = null;
        return this;
    }

    public PayloadBuilder writeString(String value) {
        buffer.writeString(value);
        packet = null;
        return this;
    }

    public PayloadBuilder writeBytes(byte[] bytes) {
        buffer.writeBytes(bytes);
        packet = null;
        return this;
    }
}
