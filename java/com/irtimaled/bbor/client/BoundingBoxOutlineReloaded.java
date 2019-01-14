package com.irtimaled.bbor.client;

import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.io.File;

public class BoundingBoxOutlineReloaded {
    private static ClientProxy proxy;

    public static void init() {
        ConfigManager.loadConfig(new File(Minecraft.getInstance().gameDir, "config"));
        proxy = new ClientProxy();
        proxy.init();
    }

    public static void chunkLoaded(Chunk chunk) {
        proxy.chunkLoaded(chunk);
    }

    public static void worldLoaded(World world) {
        proxy.worldLoaded(world);
    }

    public static void keyPressed() {
        proxy.keyPressed();
    }

    public static void render(float partialTicks) {
        proxy.tick();
        proxy.render(partialTicks);
    }

    public static void playerConnectedToServer(NetworkManager networkManager) {
        proxy.playerConnectedToServer(networkManager);
    }

    public static void playerDisconnectedFromServer() {
        proxy.playerDisconnectedFromServer();
    }
}
