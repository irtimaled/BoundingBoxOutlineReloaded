package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.models.Coords;
import io.netty.buffer.Unpooled;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PayloadBuilder {
    private static Map<String, ResourceLocation> packetNames = new HashMap<>();

    static PayloadBuilder clientBound(String name) {
        return new PayloadBuilder(packetNames.computeIfAbsent(name, ResourceLocation::new), SCustomPayloadPlayPacket::new);
    }

    static PayloadBuilder serverBound(String name) {
        return new PayloadBuilder(packetNames.computeIfAbsent(name, ResourceLocation::new), CCustomPayloadPacket::new);
    }

    private final ResourceLocation name;
    private final BiFunction<ResourceLocation, PacketBuffer, IPacket<?>> packetBuilder;
    private final PacketBuffer buffer;

    private PayloadBuilder(ResourceLocation name, BiFunction<ResourceLocation, PacketBuffer, IPacket<?>> packetBuilder) {
        this.name = name;
        this.buffer = new PacketBuffer(Unpooled.buffer());
        this.packetBuilder = packetBuilder;
    }

    private IPacket<?> packet;

    public IPacket<?> build() {
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
