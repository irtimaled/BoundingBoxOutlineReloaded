package com.irtimaled.bbor.common.messages;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.ResourceLocation;

public class SubscribeToServer {
    public static final ResourceLocation NAME = new ResourceLocation("bbor:subscribe");

    public static CPacketCustomPayload getPayload() {
        return new CPacketCustomPayload(NAME, new PacketBuffer(Unpooled.buffer()));
    }
}
