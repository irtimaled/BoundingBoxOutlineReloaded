package com.irtimaled.bbor.install;

import com.irtimaled.bbor.Versions;

import javax.swing.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Installer {
    public static void install() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        try {
            String osName = getOsName();
            File minecraftFolder = getMinecraftFolder(osName);
            String version = "BBOR-" + Versions.minecraft;
            File versionFolder = new File(minecraftFolder, "versions/" + version + "/");
            versionFolder.mkdirs();

            File versionJson = new File(versionFolder, version + ".json");
            Files.copy(Installer.class.getResourceAsStream("/profile.json"), versionJson.toPath(), StandardCopyOption.REPLACE_EXISTING);

            try {
                File profilesJson = new File(minecraftFolder, "launcher_profiles.json");
                if (profilesJson.exists()) {
                    String identifier = "\"" + version.toLowerCase() + "\"";
                    String contents = new String(Files.readAllBytes(profilesJson.toPath()));
                    if (contents.contains(identifier)) {
                        contents = contents.replaceAll(",\n?\\s*" + identifier + "\\s*:\\s*\\{[^}]*},", ",");
                        contents = contents.replaceAll(",?\n?\\s*" + identifier + "\\s*:\\s*\\{[^}]*},?", "");
                    }

                    String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                    contents = contents.replaceAll("([}],\n?\\s*\"profiles\"\\s*:\\s*[{]\n?)", "$1" +
                            "    " + identifier + ": {\n" +
                            "      \"name\": \"Bounding Box Outline Reloaded\",\n" +
                            "      \"type\": \"custom\",\n" +
                            "      \"created\": \"" + date + "T00:00:00.000Z\",\n" +
                            "      \"lastUsed\": \"2100-01-01T00:00:00.000Z\",\n" +
                            "      \"lastVersionId\": \"" + version + "\"\n" +
                            "    },\n");

                    Files.write(profilesJson.toPath(), contents.getBytes());
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                String source = Installer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                if (source.startsWith("/") && osName.contains("win")) {
                    source = source.substring(1);
                }
                File mainJar = new File(minecraftFolder, "libraries/com/irtimaled/bbor/" + Versions.minecraft + "/" + version.toLowerCase() + ".jar");
                mainJar.getParentFile().mkdirs();
                Files.copy(Paths.get(source), mainJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            JOptionPane.showMessageDialog(null,
                    "Bounding Box Outline Reloaded " + Versions.build + " for " + Versions.minecraft + " has been successfully installed!\n" +
                            "\n" +
                            "Re-open the Minecraft Launcher to see it in the dropdown.",
                    "Bounding Box Outline Reloaded Installer", JOptionPane.INFORMATION_MESSAGE);
        } catch (Throwable t) {
            StringWriter w = new StringWriter();
            t.printStackTrace(new PrintWriter(w));
            JOptionPane.showMessageDialog(null,
                    "An error occured while installing Bounding Box Outline Reloaded, please report this to the issue\n" +
                            "tracker (https://github.com/irtimaled/BoundingBoxOutlineReloaded/issues):\n" +
                            "\n" +
                            w.toString().replace("\t", "    "), "Bounding Box Outline Reloaded Installer", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static File getMinecraftFolder(String osName) {
        File minecraftFolder;
        if (osName.contains("win")) {
            minecraftFolder = new File(System.getenv("APPDATA") + "/.minecraft");
        } else if (osName.contains("mac")) {
            minecraftFolder = new File(System.getProperty("user.home") + "/Library/Application Support/minecraft");
        } else {
            minecraftFolder = new File(System.getProperty("user.home") + "/.minecraft");
        }
        return minecraftFolder;
    }

    private static String getOsName() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT);
    }
}
