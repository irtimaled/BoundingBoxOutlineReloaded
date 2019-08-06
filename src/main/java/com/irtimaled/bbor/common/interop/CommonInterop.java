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

import java.util.Collection;
import java.util.Map;

public class CommonInterop {
    public static void chunkLoaded(WorldChunk chunk) {
        DimensionId dimensionId = DimensionId.from(chunk.getWorld().getDimension().getType());
        Map<String, StructureStart> structures = chunk.getStructureStarts();
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

        ClientConnection networkManager = connection.client;
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
