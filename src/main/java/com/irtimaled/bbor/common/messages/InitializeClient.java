package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.client.events.InitializeClientReceived;
import com.irtimaled.bbor.common.models.WorldData;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.ResourceLocation;

public class InitializeClient {
    public static final ResourceLocation NAME = new ResourceLocation("bbor:initialize");

    public static SPacketCustomPayload getPayload(WorldData worldData) {
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        buf.writeLong(worldData.getSeed());
        buf.writeInt(worldData.getSpawnX());
        buf.writeInt(worldData.getSpawnZ());

        return new SPacketCustomPayload(NAME, buf);
    }

    public static InitializeClientReceived getEvent(PacketBuffer buf) {
        long seed = buf.readLong();
        int spawnX = buf.readInt();
        int spawnZ = buf.readInt();
        return new InitializeClientReceived(seed, spawnX, spawnZ);
    }
}
