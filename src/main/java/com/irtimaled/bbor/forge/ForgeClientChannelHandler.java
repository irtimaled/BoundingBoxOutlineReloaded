package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.client.interop.ClientInterop;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketSpawnPosition;

public class ForgeClientChannelHandler extends SimpleChannelInboundHandler<Packet<?>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet<?> msg) {
        if (msg instanceof SPacketSpawnPosition) {
            handle((SPacketSpawnPosition) msg);
        }
        ctx.fireChannelRead(msg);
    }

    private void handle(SPacketSpawnPosition msg) {
        ClientInterop.updateWorldSpawnReceived(msg.getSpawnPos());
    }
}

