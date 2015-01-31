package com.irtimaled.bbor;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerProxy extends CommonProxy {

    public ConcurrentHashMap<EntityPlayerMP, Integer> playerDimensions =
            new ConcurrentHashMap<EntityPlayerMP, Integer>();

    private Map<EntityPlayerMP, Set<BoundingBox>> playerBoundingBoxesCache = new HashMap<EntityPlayerMP, Set<BoundingBox>>();

    @SubscribeEvent
    public void playerJoinedWorldEvent(net.minecraftforge.event.entity.EntityJoinWorldEvent evt) {
        if (evt.entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) evt.entity;
            int dimension = player.dimension;
            playerDimensions.put(player, dimension);

            sendToPlayer(player, boundingBoxCacheMap.get(dimension));
        }
    }

    @SubscribeEvent
    public void tickEvent(TickEvent event) {
        for (EntityPlayerMP player : playerDimensions.keySet()) {

            MinecraftServer mc = MinecraftServer.getServer();
            if (!mc.getConfigurationManager().playerEntityList.contains(player)) {
                playerDimensions.remove(player);
            } else {
                int dimension = playerDimensions.get(player);
                if (boundingBoxCacheMap.containsKey(dimension)) {
                    sendToPlayer(player, boundingBoxCacheMap.get(dimension));
                }
            }
        }
    }

    private void sendToPlayer(EntityPlayerMP player, BoundingBoxCache boundingBoxCache) {
        Map<BoundingBox, Set<BoundingBox>> cacheSubset = getBoundingBoxMap(player, boundingBoxCache.getBoundingBoxes());

        int dimension = player.dimension;
        if (cacheSubset.keySet().size() > 0) {
            FMLLog.info("send %d entries to %s (%d)", cacheSubset.keySet().size(), player.getDisplayNameString(), dimension);
        }

        for (BoundingBox key : cacheSubset.keySet()) {
            Set<BoundingBox> boundingBoxes = cacheSubset.get(key);
            network.sendTo(BoundingBoxMessage.from(dimension, key, boundingBoxes), player);

            if (!playerBoundingBoxesCache.containsKey(player)) {
                playerBoundingBoxesCache.put(player, new HashSet<BoundingBox>());
            }
            playerBoundingBoxesCache.get(player).add(key);
        }
    }

    private Map<BoundingBox, Set<BoundingBox>> getBoundingBoxMap(EntityPlayerMP player, Map<BoundingBox, Set<BoundingBox>> boundingBoxMap) {
        Map<BoundingBox, Set<BoundingBox>> cacheSubset = new HashMap<BoundingBox, Set<BoundingBox>>();
        for (BoundingBox key : boundingBoxMap.keySet()) {
            if (!playerBoundingBoxesCache.containsKey(player) || !playerBoundingBoxesCache.get(player).contains(key)) {
                cacheSubset.put(key, boundingBoxMap.get(key));
            }
        }
        return cacheSubset;
    }
}