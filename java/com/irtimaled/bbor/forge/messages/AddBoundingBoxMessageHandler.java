package com.irtimaled.bbor.forge.messages;

import com.irtimaled.bbor.forge.ForgeMod;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AddBoundingBoxMessageHandler implements IMessageHandler<AddBoundingBoxMessage, IMessage> {
    @Override
    public IMessage onMessage(AddBoundingBoxMessage message, MessageContext ctx) {
        ForgeMod.proxy.addBoundingBox(message.getDimensionType(), message.getKey(), message.getBoundingBoxes());
        return null;
    }
}