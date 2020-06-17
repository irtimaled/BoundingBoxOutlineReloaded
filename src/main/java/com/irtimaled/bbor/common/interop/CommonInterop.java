package com.irtimaled.bbor.common.interop;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.events.*;
import com.irtimaled.bbor.common.models.DimensionId;
import com.irtimaled.bbor.common.models.ServerPlayer;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

public class CommonInterop {
    public static void chunkLoaded(WorldChunk chunk) {
        DimensionId dimensionId = DimensionId.from(chunk.getWorld());
        Map<StructureFeature<?>, StructureStart<?>> tmpsStructures = chunk.getStructureStarts();
        Map<String, StructureStart<?>> structures = new HashMap<>();

        for (StructureFeature<?> key : tmpsStructures.keySet()) {
            structures.put(key.getName(), tmpsStructures.get(key));
        }

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
        ServerPlayNetworkHandler connection = player.networkHandler;
        if (connection == null) return;

        ClientConnection networkManager = connection.connection;
        if (networkManager.isLocal()) return;

        EventBus.publish(new PlayerLoggedIn(new ServerPlayer(player)));
    }

    public static void playerLoggedOut(ServerPlayerEntity player) {
        EventBus.publish(new PlayerLoggedOut(player.getEntityId()));
    }

    public static void playerSubscribed(ServerPlayerEntity player) {
        EventBus.publish(new PlayerSubscribed(player.getEntityId(), new ServerPlayer(player)));
    }
}
