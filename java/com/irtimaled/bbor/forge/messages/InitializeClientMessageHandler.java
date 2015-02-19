package com.irtimaled.bbor.forge.messages;

import com.irtimaled.bbor.forge.ForgeMod;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class InitializeClientMessageHandler implements IMessageHandler<InitializeClientMessage, IMessage> {
    @Override
    public IMessage onMessage(InitializeClientMessage message, MessageContext ctx) {
        ForgeMod.proxy.setWorldData(message.getWorldData());
        return null;
    }
}
