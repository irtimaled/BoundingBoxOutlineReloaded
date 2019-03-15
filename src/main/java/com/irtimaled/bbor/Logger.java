package com.irtimaled.bbor;

import org.bukkit.Bukkit;

public class Logger {
    private static final java.util.logging.Logger logger = Bukkit.getLogger();

    public static void info(String s, Object... objects) {
        if (objects.length == 0) {
            logger.info(s);
        } else {
            logger.info(String.format(s, objects));
        }
    }
}
