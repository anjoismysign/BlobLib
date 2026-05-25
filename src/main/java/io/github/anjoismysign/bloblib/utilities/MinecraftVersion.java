package io.github.anjoismysign.bloblib.utilities;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record MinecraftVersion(int getMajor, int getMinor, int getHotfix) implements Comparable<MinecraftVersion> {
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

    /**
     * Accepts both:
     * - Legacy shorthand:  "19.4"    → major=19, minor=4,  hotfix=0 (represents 1.19.4)
     * - Legacy shorthand:  "19.4.1"  → major=19, minor=4,  hotfix=1 (represents 1.19.4.1)
     * - Legacy full:       "1.19.4"  → major=19, minor=4,  hotfix=0
     * - Legacy full:       "1.19.4.1"→ major=19, minor=4,  hotfix=1
     * - Modern format:     "26.1"    → major=26, minor=1,  hotfix=0
     * - Modern format:     "26.1.1"  → major=26, minor=1,  hotfix=1
     * <p>
     * Rule: if the first number is less than 26, it is treated as a legacy
     * version (1.MAJOR.MINOR). If it is 26 or greater, it is the new year-based format.
     */
    public static MinecraftVersion of(@NotNull String input) {
        Objects.requireNonNull(input, "'input' cannot be null");
        String[] split = input.split("\\.");
        if (split.length < 2)
            throw new IllegalArgumentException("Invalid version format: " + input);
        try {
            int first = Integer.parseInt(split[0]);

            if (first == 1) {
                // Full legacy string e.g. "1.19.4" or "1.19.4.1"
                int major = Integer.parseInt(split[1]);
                int minor = split.length > 2 ? Integer.parseInt(split[2]) : 0;
                int hotfix = split.length > 3 ? Integer.parseInt(split[3]) : 0;
                return new MinecraftVersion(major, minor, hotfix);
            } else if (first < 26) {
                // Shorthand legacy e.g. "19.4" or "19.4.1" → represents 1.19.4 / 1.19.4.1
                int major = first;
                int minor = Integer.parseInt(split[1]);
                int hotfix = split.length > 2 ? Integer.parseInt(split[2]) : 0;
                return new MinecraftVersion(major, minor, hotfix);
            } else {
                // Modern year-based format e.g. "26.1" or "26.1.1"
                int major = first;
                int minor = Integer.parseInt(split[1]);
                int hotfix = split.length > 2 ? Integer.parseInt(split[2]) : 0;
                return new MinecraftVersion(major, minor, hotfix);
            }
        } catch (NumberFormatException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public int compareTo(@NotNull MinecraftVersion other) {
        Objects.requireNonNull(other, "'other' cannot be null");
        if (this.getMajor != other.getMajor)
            return Integer.compare(this.getMajor, other.getMajor);
        if (this.getMinor != other.getMinor)
            return Integer.compare(this.getMinor, other.getMinor);
        return Integer.compare(this.getHotfix, other.getHotfix);
    }
}