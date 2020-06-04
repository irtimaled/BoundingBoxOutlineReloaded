package com.irtimaled.bbor;

public class Versions {
    public static final String minecraft;
    public static final String build;

    static {
        String version = Versions.class.getPackage().getImplementationVersion();

        if (version == null) version = "dev-dev";
        String[] versionParts = version.split("-");

        build = versionParts[0];
        minecraft = versionParts[1];
    }
}
