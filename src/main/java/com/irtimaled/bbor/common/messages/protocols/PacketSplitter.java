/*
 *
 * This file is a part of QuickCarpet, originally licensed under the MIT License (MIT).
 * https://github.com/QuickCarpet/QuickCarpet
 *
 */

package com.irtimaled.bbor.common.messages.protocols;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PacketSplitter {
    public static final int MAX_TOTAL_PER_PACKET_S2C = 1048576;
    public static final int MAX_PAYLOAD_PER_PACKET_S2C = MAX_TOTAL_PER_PACKET_S2C - 5;
    public static final int MAX_TOTAL_PER_PACKET_C2S = 32767;
    public static final int MAX_PAYLOAD_PER_PACKET_C2S = MAX_TOTAL_PER_PACKET_C2S - 5;
    public static final int DEFAULT_MAX_RECEIVE_SIZE_C2S = 1048576;
    public static final int DEFAULT_MAX_RECEIVE_SIZE_S2C = 67108864;

    private static final Map<Pair<PacketListener, Identifier>, ReadingSession> readingSessions = new ConcurrentHashMap<>();

    public static void send(ServerPlayNetworkHandler networkHandler, Identifier channel, PacketByteBuf packet) {
        send(packet, MAX_PAYLOAD_PER_PACKET_S2C, buf -> networkHandler.sendPacket(new CustomPayloadS2CPacket(channel, buf)));
    }

    @Environment(EnvType.CLIENT)
    public static void send(ClientPlayNetworkHandler networkHandler, Identifier channel, PacketByteBuf packet) {
        send(packet, MAX_PAYLOAD_PER_PACKET_C2S, buf -> networkHandler.sendPacket(new CustomPayloadC2SPacket(channel, buf)));
    }

    public static void send(PacketByteBuf packet, int payloadLimit, Consumer<PacketByteBuf> sender) {
        int len = packet.writerIndex();
        packet.resetReaderIndex();
        for (int offset = 0; offset < len; offset += payloadLimit) {
            int thisLen = Math.min(len - offset, payloadLimit);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer(thisLen + (offset == 0 ? PacketByteBuf.getVarIntLength(len) : 0)));
            buf.resetWriterIndex();
            if (offset == 0) buf.writeVarInt(len);
            buf.writeBytes(packet, thisLen);
            sender.accept(buf);
        }
        packet.release();
    }

    public static PacketByteBuf receive(ServerPlayNetworkHandler networkHandler, CustomPayloadC2SPacket message) {
        return receive(networkHandler, message, DEFAULT_MAX_RECEIVE_SIZE_C2S);
    }

    public static PacketByteBuf receive(ServerPlayNetworkHandler networkHandler, CustomPayloadC2SPacket message, int maxLength) {
        Pair<PacketListener, Identifier> key = Pair.of(networkHandler, message.getChannel());
        return readingSessions.computeIfAbsent(key, ReadingSession::new).receive(message.getData(), maxLength);
    }

    public static PacketByteBuf receive(ClientPlayPacketListener networkHandler, CustomPayloadS2CPacket message) {
        return receive(networkHandler, message, DEFAULT_MAX_RECEIVE_SIZE_S2C);
    }

    public static PacketByteBuf receive(ClientPlayPacketListener networkHandler, CustomPayloadS2CPacket message, int maxLength) {
        Pair<PacketListener, Identifier> key = Pair.of(networkHandler, message.getChannel());
        return readingSessions.computeIfAbsent(key, ReadingSession::new).receive(message.getData(), maxLength);
    }

    private static class ReadingSession {
        private final Pair<PacketListener, Identifier> key;
        private int expectedSize = -1;
        private PacketByteBuf received;
        private ReadingSession(Pair<PacketListener, Identifier> key) {
            this.key = key;
        }

        private PacketByteBuf receive(PacketByteBuf data, int maxLength) {
            if (expectedSize < 0) {
                expectedSize = data.readVarInt();
                if (expectedSize > maxLength) throw new IllegalArgumentException("Payload too large");
                received = new PacketByteBuf(Unpooled.buffer(expectedSize));
            }
            received.writeBytes(data.readBytes(data.readableBytes()));
            if (received.writerIndex() >= expectedSize) {
                readingSessions.remove(key);
                return received;
            }
            return null;
        }
    }
}
