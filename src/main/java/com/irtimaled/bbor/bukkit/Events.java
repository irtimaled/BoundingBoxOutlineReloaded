package com.irtimaled.bbor.bukkit;

import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.messages.SubscribeToServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class Events implements Listener, PluginMessageListener {
    private boolean active;

    @EventHandler
    public void onChunkLoad(@NotNull ChunkLoadEvent event) {
        if (!active) return;

        net.minecraft.world.level.chunk.Chunk chunk = VersionHelper.getNMSChunk(event.getChunk());
        if (chunk != null) {
            CommonInterop.chunkLoaded(chunk);
        }
    }

    @EventHandler
    public void onWorldLoad(@NotNull WorldLoadEvent event) {
        if (!active) return;

        WorldServer world = VersionHelper.getNMSWorld(event.getWorld());
        if (world != null) {
            CommonInterop.loadWorld(world);
            CommonInterop.loadWorldStructures(world);

            for (Chunk chunk : event.getWorld().getLoadedChunks()) {
                net.minecraft.world.level.chunk.Chunk nmsChunk = VersionHelper.getNMSChunk(chunk);
                if (nmsChunk != null) {
                    CommonInterop.chunkLoaded(nmsChunk);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLoggedIn(@NotNull PlayerJoinEvent event) {
        if (!active) return;

        EntityPlayer player = VersionHelper.getNMSPlayer(event.getPlayer());
        if (player != null) {
            CommonInterop.playerLoggedIn(player);
        }
    }

    @EventHandler
    public void onPlayerLoggedOut(@NotNull PlayerQuitEvent event) {
        if (!active) return;

        EntityPlayer player = VersionHelper.getNMSPlayer(event.getPlayer());
        if (player != null) {
            CommonInterop.playerLoggedOut(player);
        }
    }

    void enable() {
        this.active = true;
    }

    void disable() {
        this.active = false;
    }

    void onTick() {
        if (!active) return;

        CommonInterop.tick();
    }

    @Override
    public void onPluginMessageReceived(@NotNull String string, @NotNull Player player, byte[] bytes) {
        if (!active) return;

        if (string.equals(SubscribeToServer.NAME)) {
            EntityPlayer entityPlayer = VersionHelper.getNMSPlayer(player);
            if (entityPlayer != null) {
                CommonInterop.playerSubscribed(entityPlayer);
            }
        }
    }
}
