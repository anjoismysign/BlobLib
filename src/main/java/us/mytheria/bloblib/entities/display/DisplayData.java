package us.mytheria.bloblib.entities.display;

import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;

/**
 * Holds all data that DisplayEntities hold except
 * data related to Transformations.
 *
 * @param brightness
 * @param billboard
 * @param shadowRadius
 * @param shadowStrength
 */
public record DisplayData(Display.Brightness brightness,
                          Display.Billboard billboard,
                          float shadowRadius,
                          float shadowStrength,
                          float viewRange,
                          float displayWidth,
                          float displayHeight,
                          @Nullable Color glowColorOverride,
                          boolean applyBrightness,
                          int interpolationDuration,
                          int interpolationDelay,
                          int teleportDuration) {

    public static DisplayData DEFAULT = new DisplayData(new Display.Brightness(15, 15),
            Display.Billboard.FIXED, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f,
            null, false,
            1, 0, 1);

    /**
     * Gets a DisplayData instance from the given Display.
     *
     * @param display the Display to get the DisplayData from
     * @return the DisplayData from the given Display
     */
    public static DisplayData of(@NotNull Display display) {
        Objects.requireNonNull(display, "'display' cannot be null");
        return new DisplayData(display.getBrightness(), display.getBillboard(),
                display.getShadowRadius(), display.getShadowStrength(),
                display.getViewRange(), display.getDisplayWidth(), display.getDisplayHeight(),
                display.getGlowColorOverride(), true,
                display.getInterpolationDuration(), display.getInterpolationDelay(),
                display.getTeleportDuration());
    }

    /**
     * Will apply the DisplayData to the given Display.
     *
     * @param display the Display to apply the DisplayData to
     */
    public void apply(Display display) {
        if (applyBrightness)
            display.setBrightness(brightness);
        display.setBillboard(billboard);
        display.setShadowRadius(shadowRadius);
        display.setShadowStrength(shadowStrength);
        display.setViewRange(viewRange);
        display.setDisplayWidth(displayWidth);
        display.setDisplayHeight(displayHeight);
        display.setGlowColorOverride(glowColorOverride);
        display.setInterpolationDuration(interpolationDuration);
        display.setInterpolationDelay(interpolationDelay);
        try {
            display.setTeleportDuration(teleportDuration);
        } catch (Throwable ignored) {
        }
    }

    /**
     * Will read the Display-Data section from the given ConfigurationSection.
     * If ConfigurationSection is missing the Brightness or Billboard values,
     * will fail fast. Regarding the other values, default values will be
     * provided if missing.
     *
     * @param section the ConfigurationSection to read from
     * @return the DisplayData from the given ConfigurationSection
     */
    public static DisplayData of(ConfigurationSection section) {
        Display.Brightness brightness = DisplayReader.BRIGHTNESS_DEFAULT(section);
        Display.Billboard billboard = DisplayReader.BILLBOARD_DEFAULT(section);
        float shadowRadius = (float) section.getDouble("Shadow-Radius", 0.0);
        float shadowStrength = (float) section.getDouble("Shadow-Strength", 1.0);
        float viewRange = (float) section.getDouble("View-Range", 1.0);
        float displayWidth = (float) section.getDouble("Display-Width", 0.0);
        float displayHeight = (float) section.getDouble("Display-Height", 0.0);
        boolean applyBrightness = section.getBoolean("Apply-Brightness", false);
        Color color = null;
        if (section.isString("Glow-Color-Override")) {
            String rgb = section.getString("Glow-Color-Override");
            String[] split = rgb.split(",");
            if (split.length == 1)
                throw new IllegalArgumentException("Glow-Color-Override is not in the correct format");
            int r = Integer.parseInt(split[0]);
            int g = Integer.parseInt(split[1]);
            int b = Integer.parseInt(split[2]);
            color = Color.fromRGB(r, g, b);
        }
        int interpolationDuration = section.getInt("Interpolation-Duration", 1);
        int interpolationDelay = section.getInt("Interpolation-Delay", 0);
        int teleportDuration = section.getInt("Teleport-Duration", 1);
        return new DisplayData(brightness, billboard, shadowRadius, shadowStrength,
                viewRange, displayWidth, displayHeight, color, applyBrightness,
                interpolationDuration, interpolationDelay, teleportDuration);
    }

    /**
     * Will read the Display-Data section from the given FileConfiguration.
     * If FileConfiguration is missing the Display-Data section,
     * will fail fast.
     *
     * @param configuration the FileConfiguration to read from
     * @return the DisplayData from the given FileConfiguration
     */
    public static DisplayData of(FileConfiguration configuration) {
        if (!configuration.isConfigurationSection("Display-Data"))
            throw new IllegalArgumentException("Display-Data section is missing");
        return of(configuration.getConfigurationSection("Display-Data"));
    }

    /**
     * Will read the Display-Data section from the given File.
     * If file is not a .yml file, will fail fast.
     *
     * @param file the File to read from
     * @return the DisplayData from the given File
     */
    public static DisplayData of(File file) {
        String name = file.getName();
        if (!name.endsWith(".yml"))
            throw new IllegalArgumentException("File is not a .yml file");
        return of(YamlConfiguration.loadConfiguration(file));
    }

    public void write(ConfigurationSection section) {
        DisplayWriter.WRITE(section, brightness, applyBrightness);
        DisplayWriter.WRITE(section, billboard);
        section.set("Shadow-Radius", shadowRadius);
        section.set("Shadow-Strength", shadowStrength);
        section.set("View-Range", viewRange);
        section.set("Display-Width", displayWidth);
        section.set("Display-Height", displayHeight);
        if (glowColorOverride != null)
            section.set("Glow-Color-Override", glowColorOverride.getRed() + "," +
                    glowColorOverride.getGreen() + "," + glowColorOverride.getBlue());
    }
}
