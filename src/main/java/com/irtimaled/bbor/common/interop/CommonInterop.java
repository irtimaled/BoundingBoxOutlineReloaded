package com.irtimaled.bbor.common.interop;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.events.*;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.ServerPlayer;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import java.io.File;

public class CommonInterop {
    public static void init() {
        ConfigManager.loadConfig(new File("."));
    }

    public static void chunkLoaded(Chunk chunk) {
        EventBus.publish(new ChunkLoaded(chunk));
    }

    public static void loadWorlds(WorldServer[] worlds) {
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

    public static void tryHarvestBlock(Block block, BlockPos pos, World world) {
        if (block instanceof BlockMobSpawner) {
            EventBus.publish(new MobSpawnerBroken(world.provider.getDimensionType().getId(), new Coords(pos)));
        }
    }
}
