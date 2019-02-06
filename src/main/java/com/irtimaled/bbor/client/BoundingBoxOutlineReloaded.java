package com.irtimaled.bbor.client;

import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
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
        if (ActiveHotKey.isPressed()) {
            proxy.toggleActive();
        } else if (OuterBoxOnlyHotKey.isPressed()) {
            proxy.toggleOuterBoxOnly();
        }
    }

    public static final String KeyCategory = "Bounding Box Outline Reloaded";
    public static KeyBinding ActiveHotKey =  new KeyBinding("Toggle On/Off", 0x42, KeyCategory);
    public static KeyBinding OuterBoxOnlyHotKey= new KeyBinding("Toggle Display Outer Box Only", 0x4f, KeyCategory);

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
