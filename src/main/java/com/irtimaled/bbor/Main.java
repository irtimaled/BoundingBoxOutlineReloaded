package com.irtimaled.bbor;

import com.irtimaled.bbor.install.Installer;
import com.irtimaled.bbor.server.ServerRunner;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String... args) throws IOException {
        if (args.length > 0 && args[0].equals("--server")) {
            ServerRunner.run("@MC_VERSION@", Arrays.asList(args).subList(1, args.length));
        } else {
            Installer.install("@VERSION@", "@MC_VERSION@");
        }
    }
}
