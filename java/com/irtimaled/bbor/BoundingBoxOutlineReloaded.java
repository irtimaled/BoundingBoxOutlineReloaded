package com.ostlerdev.bbreloaded;

import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.io.File;

public class BoundingBoxOutlineReloaded {

    public static ClientProxy proxy;

    public static void init() {
        proxy = new ClientProxy();
        proxy.init(new ConfigManager(new File(Minecraft.getMinecraft().mcDataDir, "config")));
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
        proxy.render(partialTicks);
    }

    public static void playerConnectedToServer(NetworkManager networkManager) {
        proxy.playerConnectedToServer(networkManager);
    }

    public static void playerDisconnectedFromServer() {
        proxy.playerDisconnectedFromServer();
    }
}


