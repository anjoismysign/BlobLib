package us.mytheria.bloblib.utilities;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record MinecraftVersion(int getMajor, int getMinor) implements Comparable<MinecraftVersion> {
    private static MinecraftVersion running;

    public static MinecraftVersion getRunning() {
        if (running == null) {
            String version = Bukkit.getVersion();
            String[] split = version.split("MC: ");
            if (split.length == 1)
                throw new IllegalStateException("Failed to get Minecraft version");
            else {
                version = split[1].substring(0, split[1].length() - 1);
                running = MinecraftVersion.of(version);
            }
        }
        return running;
    }

    public static MinecraftVersion of(@NotNull String input) {
        Objects.requireNonNull(input, "'input' cannot be null");
        String[] split = input.split("\\.");
        if (split.length < 2)
            throw new IllegalArgumentException("Invalid version format: " + input);
        String major;
        String minor;
        try {
            if (split.length > 2) {
                major = split[1];
                minor = split[2];
                int x = Integer.parseInt(major);
                int y = Integer.parseInt(minor);
                return new MinecraftVersion(x, y);
            }
            major = split[1];
            int x = Integer.parseInt(major);
            int y = 0;
            return new MinecraftVersion(x, y);
        } catch ( NumberFormatException exception ) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public int compareTo(@NotNull MinecraftVersion other) {
        Objects.requireNonNull(other, "'other' cannot be null");
        if (this.getMajor == other.getMajor && this.getMinor == other.getMinor)
            return 0;
        if (this.getMajor > other.getMajor)
            return 1;
        if (this.getMajor < other.getMajor)
            return -1;
        if (this.getMinor > other.getMinor)
            return 1;
        return -1;
    }
}
