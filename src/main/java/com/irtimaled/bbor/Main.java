package com.irtimaled.bbor;

import com.irtimaled.bbor.install.Installer;

public class Main {
    public static void main(String... args) {
        Installer.install("@VERSION@", "@MC_VERSION@");
    }
}
