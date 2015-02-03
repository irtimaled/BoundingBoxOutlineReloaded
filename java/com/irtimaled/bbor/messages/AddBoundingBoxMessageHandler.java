package com.irtimaled.bbor.messages;

import com.irtimaled.bbor.BoundingBoxCache;
import com.irtimaled.bbor.BoundingBoxOutlineReloaded;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Map;

public class AddBoundingBoxMessageHandler implements IMessageHandler<AddBoundingBoxMessage, IMessage> {
    @Override
    public IMessage onMessage(AddBoundingBoxMessage message, MessageContext ctx) {

        Map<Integer, BoundingBoxCache> boundingBoxCacheMap = BoundingBoxOutlineReloaded.proxy.boundingBoxCacheMap;
        int dimension = message.getDimension();
        if (!boundingBoxCacheMap.containsKey(dimension)) {
            boundingBoxCacheMap.put(dimension, new BoundingBoxCache());
        }

        boundingBoxCacheMap.get(dimension).addBoundingBox(message.getKey(), message.getBoundingBoxes());
        return null;
    }
}