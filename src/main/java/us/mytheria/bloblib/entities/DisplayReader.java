package us.mytheria.bloblib.entities;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;

public class DisplayReader {
    public static Display.Brightness BRIGHTNESS_FAIL_FAST(ConfigurationSection section) {
        if (!section.isInt("Block-Light"))
            throw new IllegalArgumentException("Block-Light is not valid");
        if (!section.isInt("Sky-Light"))
            throw new IllegalArgumentException("Sky-Light is not valid");
        return new Display.Brightness(section.getInt("Block-Light"), section.getInt("Sky-Light"));
    }

    public static Display.Brightness BRIGHTNESS_DEFAULT(ConfigurationSection section) {
        int blockLight = section.getInt("Block-Light", 15);
        int skyLight = section.getInt("Sky-Light", 15);
        return new Display.Brightness(blockLight, skyLight);
    }

    public static Display.Billboard BILLBOARD_FAIL_FAST(ConfigurationSection section) {
        if (!section.isString("Billboard"))
            throw new IllegalArgumentException("Billboard is not valid");
        return Display.Billboard.valueOf(section.getString("Billboard"));
    }

    public static Display.Billboard BILLBOARD_DEFAULT(ConfigurationSection section) {
        return Display.Billboard.valueOf(section.getString("Billboard", "FIXED"));
    }
}
