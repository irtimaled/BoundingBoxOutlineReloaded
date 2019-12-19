package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.models.Coords;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PayloadBuilder {
    private static Map<String, Identifier> packetNames = new HashMap<>();

    static PayloadBuilder clientBound(String name) {
        return new PayloadBuilder(packetNames.computeIfAbsent(name, Identifier::new), CustomPayloadS2CPacket::new);
    }

    static PayloadBuilder serverBound(String name) {
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

    PayloadBuilder writeBoolean(boolean value) {
        buffer.writeBoolean(value);
        packet = null;
        return this;
    }

    PayloadBuilder writeCoords(Coords coords) {
        return writeVarInt(coords.getX())
                .writeVarInt(coords.getY())
                .writeVarInt(coords.getZ());
    }
}
