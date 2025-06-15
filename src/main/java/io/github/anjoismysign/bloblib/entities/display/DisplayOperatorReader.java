package io.github.anjoismysign.bloblib.entities.display;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;

public class DisplayOperatorReader {

    public static DisplayOperator READ(ConfigurationSection section, String path, JavaPlugin plugin) {
        DisplayData displayData;
        try {
            displayData = DisplayData.of(section);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage() + " in file " + path);
        }
        Transformation transformation;
        try {
            transformation = DisplayReader.TRANSFORMATION_FAIL_FAST(section);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage() + " in file " + path);
        }
        return new DisplayOperator() {
            @Override
            public DisplayData getDisplayData() {
                return displayData;
            }

            @Override
            public Transformation getTransformation() {
                return transformation;
            }

            @Override
            public Plugin getPlugin() {
                return plugin;
            }
        };
    }
}
