package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.messages.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketSpawnPosition;

public class ForgeClientChannelHandler extends SimpleChannelInboundHandler<Packet<?>> {
    private final NetworkManager networkManager;

    ForgeClientChannelHandler(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet<?> msg) {
        if (msg instanceof SPacketCustomPayload && handle((SPacketCustomPayload) msg)) {
            return;
        }
        if (msg instanceof SPacketSpawnPosition) {
            handle((SPacketSpawnPosition) msg);
        }
        ctx.fireChannelRead(msg);
    }

    private boolean handle(SPacketCustomPayload msg) {
        String channelName = msg.getChannelName();
        if (!channelName.startsWith("bbor")) {
            return false;
        }
        PacketBuffer data = null;
        try {
            data = msg.getBufferData();
            PayloadReader reader = new PayloadReader(data);
            switch (channelName) {
                case AddBoundingBox.NAME: {
                    EventBus.publish(AddBoundingBox.getEvent(reader));
                    break;
                }
                case RemoveBoundingBox.NAME: {
                    EventBus.publish(RemoveBoundingBox.getEvent(reader));
                    break;
                }
                case InitializeClient.NAME: {
                    EventBus.publish(InitializeClient.getEvent(reader));
                    networkManager.sendPacket(SubscribeToServer.getPayload().build());
                    break;
                }
            }

        } finally {
            if (data != null)
                data.release();
        }
        return true;
    }

    private void handle(SPacketSpawnPosition msg) {
        ClientInterop.updateWorldSpawnReceived(msg.getSpawnPos());
    }
}
