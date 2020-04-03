package com.irtimaled.bbor.common.interop;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.events.*;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.ServerPlayer;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.block.Block;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import java.io.File;
import java.util.Collection;

public class CommonInterop {
    public static void init() {
        ConfigManager.loadConfig(new File("."));
    }

    public static void chunkLoaded(WorldChunk chunk) {
        EventBus.publish(new ChunkLoaded(chunk));
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

    public static void tryHarvestBlock(Block block, BlockPos pos, World world) {
        if (block instanceof SpawnerBlock) {
            EventBus.publish(new MobSpawnerBroken(world.dimension.getType().getRawId(), new Coords(pos)));
        }
    }
}
