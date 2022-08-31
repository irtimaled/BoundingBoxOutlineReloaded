package com.irtimaled.bbor.bukkit;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class VersionHelper {

    public static final int lowestSupportVersion = 11900;
    public static final int lowestUnSupportVersion = 12000;

    private static final Map<Class<?>, Class<?>> craftClassCache = new HashMap<>();
    private static final Map<Class<?>, Method> craftMethodCache = new HashMap<>();

    public static boolean init(@NotNull JavaPlugin plugin) {
        try {
            String packVersion = getPackVersion();

            craftClassCache.put(Chunk.class, Class.forName("org.bukkit.craftbukkit." + packVersion + ".CraftChunk"));
            craftClassCache.put(World.class, Class.forName("org.bukkit.craftbukkit." + packVersion + ".CraftWorld"));
            craftClassCache.put(Player.class, Class.forName("org.bukkit.craftbukkit." + packVersion + ".entity.CraftPlayer"));

            craftMethodCache.put(Chunk.class, craftClassCache.get(Chunk.class).getDeclaredMethod("getHandle"));
            craftMethodCache.put(World.class, craftClassCache.get(World.class).getDeclaredMethod("getHandle"));
            craftMethodCache.put(Player.class, craftClassCache.get(Player.class).getDeclaredMethod("getHandle"));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static int getVersion() {
        String[] version = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        if (version.length < 3) {
            return Integer.parseInt(version[0]) * 10000 + Integer.parseInt(version[1]) * 100;
        } else {
            return Integer.parseInt(version[0]) * 10000 + Integer.parseInt(version[1]) * 100 + Integer.parseInt(version[2]);

        }
    }

    @NotNull
    public static String getPackVersion() {
        String[] versionPath = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        String version = "v" + versionPath[0] + "_" + versionPath[1] + "_R" + (versionPath.length > 2 ? versionPath[2] : "1");
        return version.equals("v1_19_R2") ? "v1_19_R1" : version;
    }

    @Nullable
    public static net.minecraft.world.level.chunk.Chunk getNMSChunk(@NotNull Chunk chunk) {
        try {
            return (net.minecraft.world.level.chunk.Chunk) craftMethodCache.get(Chunk.class).invoke(craftClassCache.get(Chunk.class).cast(chunk));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static WorldServer getNMSWorld(@NotNull World world) {
        try {
            return (WorldServer) craftMethodCache.get(World.class).invoke(craftClassCache.get(World.class).cast(world));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static EntityPlayer getNMSPlayer(@NotNull Player player) {
        try {
            return (EntityPlayer) craftMethodCache.get(Player.class).invoke(craftClassCache.get(Player.class).cast(player));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
