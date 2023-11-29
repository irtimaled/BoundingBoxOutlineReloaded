package com.irtimaled.bbor.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BBORCustomPayload(PacketByteBuf byteBuf, Identifier id) implements CustomPayload {

    public BBORCustomPayload(Identifier identifier, PacketByteBuf buf) {
        this(new PacketByteBuf(buf.readBytes(buf.readableBytes())), identifier);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBytes(byteBuf);
    }
}
