package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.messages.SubscribeToServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCustomPayload;

public class ForgeServerChannelHandler extends SimpleChannelInboundHandler<Packet<?>> {
    private final EntityPlayerMP player;

    ForgeServerChannelHandler(EntityPlayerMP player) {
        this.player = player;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet<?> msg) {
        if (msg instanceof CPacketCustomPayload && handle((CPacketCustomPayload) msg)) {
            return;
        }
        ctx.fireChannelRead(msg);

    }

    private boolean handle(CPacketCustomPayload msg) {
        if (!msg.getChannelName().equals(SubscribeToServer.NAME))
            return false;

        CommonInterop.playerSubscribed(player);
        return true;
    }
}
