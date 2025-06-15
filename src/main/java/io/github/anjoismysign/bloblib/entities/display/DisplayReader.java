package io.github.anjoismysign.bloblib.entities.display;

import io.github.anjoismysign.bloblib.entities.JOMLReader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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

    @Nullable
    public static Display.Brightness BRIGHTNESS_NULL(ConfigurationSection section) {
        if (!section.isInt("Block-Light"))
            return null;
        if (!section.isInt("Sky-Light"))
            return null;
        return new Display.Brightness(section.getInt("Block-Light"), section.getInt("Sky-Light"));
    }

    public static Display.Billboard BILLBOARD_FAIL_FAST(ConfigurationSection section) {
        if (!section.isString("Billboard"))
            throw new IllegalArgumentException("Billboard is not valid");
        return Display.Billboard.valueOf(section.getString("Billboard"));
    }

    public static Display.Billboard BILLBOARD_DEFAULT(ConfigurationSection section) {
        return Display.Billboard.valueOf(section.getString("Billboard", "FIXED"));
    }

    @Nullable
    public static Display.Billboard BILLBOARD_NULL(ConfigurationSection section) {
        if (!section.isString("Billboard"))
            return null;
        return Display.Billboard.valueOf(section.getString("Billboard"));
    }

    public static Transformation TRANSFORMATION_FAIL_FAST(ConfigurationSection section) {
        Vector3f scale = JOMLReader.READ_VECTOR3F(section, "Scale");
        Vector3f translation = JOMLReader.READ_VECTOR3F(section, "Translation");
        Quaternionf leftRotation = JOMLReader.READ_QUATERNIONF(section, "Left-Rotation");
        Quaternionf rightRotation = JOMLReader.READ_QUATERNIONF(section, "Right-Rotation");
        return new Transformation(translation, leftRotation, scale, rightRotation);
    }

    public static Transformation TRANSFORMATION_DEFAULT(ConfigurationSection section) {
        Vector3f scale = new Vector3f((float) section.getDouble("Scale.X", 1), (float) section.getDouble("Scale.Y", 1), (float) section.getDouble("Scale.Z", 1));
        Vector3f translation = new Vector3f((float) section.getDouble("Translation.X", 0.5), (float) section.getDouble("Translation.Y", 0.5), (float) section.getDouble("Translation.Z", 0.5));
        Quaternionf leftRotation = new Quaternionf((float) section.getDouble("Left-Rotation.X", 0), (float) section.getDouble("Left-Rotation.Y", 0), (float) section.getDouble("Left-Rotation.Z", 0), (float) section.getDouble("Left-Rotation.W", 1));
        Quaternionf rightRotation = new Quaternionf((float) section.getDouble("Right-Rotation.X", 0), (float) section.getDouble("Right-Rotation.Y", 0), (float) section.getDouble("Right-Rotation.Z", 0), (float) section.getDouble("Right-Rotation.W", 1));
        return new Transformation(translation, leftRotation, scale, rightRotation);
    }

    @Nullable
    public static Transformation TRANSFORMATION_NULL(ConfigurationSection configuration) {
        Transformation transformation;
        try {
            transformation = TRANSFORMATION_FAIL_FAST(configuration);
        } catch (Exception e) {
            return null;
        }
        return transformation;
    }
}
