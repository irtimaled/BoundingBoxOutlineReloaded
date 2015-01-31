package com.irtimaled.bbor;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Map;

public class BoundingBoxMessageHandler implements IMessageHandler<BoundingBoxMessage, IMessage> {
    @Override
    public IMessage onMessage(BoundingBoxMessage message, MessageContext ctx) {

        Map<Integer, BoundingBoxCache> boundingBoxCacheMap = BoundingBoxOutlineReloaded.proxy.boundingBoxCacheMap;
        int dimension = message.getDimension();
        if (!boundingBoxCacheMap.containsKey(dimension)) {
            boundingBoxCacheMap.put(dimension, new BoundingBoxCache());
        }

        boundingBoxCacheMap.get(dimension).getBoundingBoxes().put(message.getKey(), message.getBoundingBoxes());
        return null;
    }
}