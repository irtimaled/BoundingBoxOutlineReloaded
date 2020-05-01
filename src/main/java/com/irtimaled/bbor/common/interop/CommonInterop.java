package com.irtimaled.bbor.common.interop;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.events.*;
import com.irtimaled.bbor.common.models.ServerPlayer;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.structure.StructureStart;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public class CommonInterop {
    public static void init() {
        ConfigManager.loadConfig(new File("."));
    }

    public static void chunkLoaded(Chunk chunk) {
        int dimensionId = chunk.getWorld().getDimension().getType().getId();
        Map<String, StructureStart> structures = chunk.getStructureStarts();
        if(structures.size() > 0) EventBus.publish(new StructuresLoaded(structures, dimensionId));
    }

    public static void loadWorlds(Collection<WorldServer> worlds) {
        for (WorldServer world : worlds) {
            loadWorld(world);
        }
    }

    public static void loadWorld(WorldServer world) {
        EventBus.publish(new WorldLoaded(world));
    }

    public static void tick() {
        EventBus.publish(new ServerTick());
    }

    public static void worldTick(WorldServer worldServer) {
        EventBus.publish(new ServerWorldTick(worldServer));
    }

    public static void playerLoggedIn(EntityPlayerMP player) {
        NetHandlerPlayServer connection = player.connection;
        if (connection == null) return;

        NetworkManager networkManager = connection.netManager;
        if (networkManager.isLocalChannel()) return;

        EventBus.publish(new PlayerLoggedIn(new ServerPlayer(player)));
    }

    public static void playerLoggedOut(EntityPlayerMP player) {
        EventBus.publish(new PlayerLoggedOut(player.getEntityId()));
    }

    public static void playerSubscribed(EntityPlayerMP player) {
        EventBus.publish(new PlayerSubscribed(player.getEntityId(), new ServerPlayer(player)));
    }
}
