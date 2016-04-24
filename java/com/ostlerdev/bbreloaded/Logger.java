package com.ostlerdev.bbreloaded;

import org.apache.logging.log4j.LogManager;

public class Logger {
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();


    public static void info(String s, Object... objects) {
        if (objects.length == 0)
            logger.info(s);
        else
            logger.info(String.format(s, objects));
    }
}