package com.irtimaled.bbor.common.interop;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.events.*;
import com.irtimaled.bbor.common.models.DimensionId;
import com.irtimaled.bbor.common.models.ServerPlayer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommonInterop {
    public static void chunkLoaded(Chunk chunk) {
        DimensionId dimensionId = DimensionId.from(chunk.getWorld().getDimensionKey());
        Map<String, StructureStart<?>> structures = new HashMap<>();
        chunk.getStructureStarts().entrySet().forEach(es -> structures.put(es.getKey().getStructureName(), es.getValue()));;
        if (structures.size() > 0) EventBus.publish(new StructuresLoaded(structures, dimensionId));
    }

    public static void loadWorlds(Collection<ServerWorld> worlds) {
        for (ServerWorld world : worlds) {
            loadWorld(world);
        }
    }

    public static void loadWorld(ServerWorld world) {
        EventBus.publish(new WorldLoaded(world));
    }

    public static void tick() {
        EventBus.publish(new ServerTick());
    }

    public static void playerLoggedIn(ServerPlayerEntity player) {
        ServerPlayNetHandler connection = player.connection;
        if (connection == null) return;

        NetworkManager networkManager = connection.netManager;
        if (networkManager.isLocalChannel()) return;

        EventBus.publish(new PlayerLoggedIn(new ServerPlayer(player)));
    }

    public static void playerLoggedOut(ServerPlayerEntity player) {
        EventBus.publish(new PlayerLoggedOut(player.getEntityId()));
    }

    public static void playerSubscribed(ServerPlayerEntity player) {
        EventBus.publish(new PlayerSubscribed(player.getEntityId(), new ServerPlayer(player)));
    }
}
