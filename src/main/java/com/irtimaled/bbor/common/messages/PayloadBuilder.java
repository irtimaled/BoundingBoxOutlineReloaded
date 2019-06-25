package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.models.Coords;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomPayload;

import java.util.function.BiFunction;

public class PayloadBuilder {
    static PayloadBuilder clientBound(String name) {
        return new PayloadBuilder(name, SPacketCustomPayload::new);
    }

    static PayloadBuilder serverBound(String name) {
        return new PayloadBuilder(name, CPacketCustomPayload::new);
    }

    private final String name;
    private final BiFunction<String, PacketBuffer, Packet<?>> packetBuilder;
    private final PacketBuffer buffer;

    private PayloadBuilder(String name, BiFunction<String, PacketBuffer, Packet<?>> packetBuilder) {
        this.name = name;
        this.buffer = new PacketBuffer(Unpooled.buffer());
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
