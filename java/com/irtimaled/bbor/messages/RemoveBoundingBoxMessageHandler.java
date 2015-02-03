package com.irtimaled.bbor.messages;

import com.irtimaled.bbor.BoundingBoxCache;
import com.irtimaled.bbor.BoundingBoxOutlineReloaded;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Map;

public class RemoveBoundingBoxMessageHandler implements IMessageHandler<RemoveBoundingBoxMessage, IMessage> {
    @Override
    public IMessage onMessage(RemoveBoundingBoxMessage message, MessageContext ctx) {

        Map<Integer, BoundingBoxCache> boundingBoxCacheMap = BoundingBoxOutlineReloaded.proxy.boundingBoxCacheMap;
        int dimension = message.getDimension();
        if (boundingBoxCacheMap.containsKey(dimension)) {
            boundingBoxCacheMap.get(dimension).removeBoundingBox(message.getKey());
        }
        return null;
    }
}