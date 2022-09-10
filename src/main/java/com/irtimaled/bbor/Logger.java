package com.irtimaled.bbor;

import com.irtimaled.bbor.bukkit.BukkitMod;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Logger {

    private static java.util.logging.Logger logger = null;

    public static void init(@NotNull JavaPlugin plugin) {
        if (logger == null) {
            logger = plugin.getLogger();
        }
    }

    public static void info(String s, Object @NotNull ... objects) {
        if (objects.length == 0) {
            logger.info(s);
        } else {
            logger.info(String.format(s, objects));
        }
    }

    public static void warn(String s, Object @NotNull ... objects) {
        if (objects.length == 0) {
            logger.warning(s);
        } else {
            logger.warning(String.format(s, objects));
        }
    }

    public static void error(String s, Object @NotNull ... objects) {
        if (objects.length == 0) {
            logger.severe(s);
        } else {
            logger.severe(String.format(s, objects));
        }
    }
}
