package com.irtimaled.bbor.messages;

import com.irtimaled.bbor.BoundingBoxOutlineReloaded;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class InitializeClientMessageHandler implements IMessageHandler<InitializeClientMessage, IMessage> {
    @Override
    public IMessage onMessage(InitializeClientMessage message, MessageContext ctx) {
        BoundingBoxOutlineReloaded.proxy.setWorldData(message);
        return null;
    }
}
