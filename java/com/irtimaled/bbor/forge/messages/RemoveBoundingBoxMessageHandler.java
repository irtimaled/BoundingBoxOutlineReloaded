package com.irtimaled.bbor.forge.messages;

import com.irtimaled.bbor.forge.ForgeMod;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RemoveBoundingBoxMessageHandler implements IMessageHandler<RemoveBoundingBoxMessage, IMessage> {
    @Override
    public IMessage onMessage(RemoveBoundingBoxMessage message, MessageContext ctx) {

        ForgeMod.proxy.removeBoundingBox(message.getDimension(), message.getKey());
        return null;
    }
}