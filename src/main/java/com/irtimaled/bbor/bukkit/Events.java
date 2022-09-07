package com.irtimaled.bbor.bukkit;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.messages.SubscribeToServer;
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
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!active) return;

        Object chunk = NMSHelper.getNMSChunk(event.getChunk());
        if (chunk != null) {
            CommonInterop.chunkLoaded(chunk);
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (!active) return;

        Object world = NMSHelper.getNMSWorld(event.getWorld());
        if (world != null) {
            CommonInterop.loadWorld(world);
            CommonInterop.loadWorldStructures(world);

            for (Chunk chunk : event.getWorld().getLoadedChunks()) {
                Object nmsChunk = NMSHelper.getNMSChunk(chunk);
                if (nmsChunk != null) {
                    CommonInterop.chunkLoaded(nmsChunk);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLoggedIn(PlayerJoinEvent event) {
        if (!active) return;

        Object player = NMSHelper.getNMSPlayer(event.getPlayer());
        if (player != null) {
            CommonInterop.playerLoggedIn(player);
        }
    }

    @EventHandler
    public void onPlayerLoggedOut(PlayerQuitEvent event) {
        if (!active) return;

        Object player = NMSHelper.getNMSPlayer(event.getPlayer());
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
            Object entityPlayer = NMSHelper.getNMSPlayer(player);
            if (entityPlayer != null) {
                CommonInterop.playerSubscribed(entityPlayer);
            }
        }
    }
}
