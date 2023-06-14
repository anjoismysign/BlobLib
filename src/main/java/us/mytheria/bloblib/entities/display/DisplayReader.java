package us.mytheria.bloblib.entities.display;

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
        if (!section.isConfigurationSection("Scale"))
            throw new IllegalArgumentException("Scale is not valid");
        if (!section.isConfigurationSection("Translation"))
            throw new IllegalArgumentException("Translation is not valid");
        if (!section.isConfigurationSection("Left-Rotation"))
            throw new IllegalArgumentException("Left-Rotation is not valid");
        if (!section.isConfigurationSection("Right-Rotation"))
            throw new IllegalArgumentException("Right-Rotation is not valid");
        ConfigurationSection scaleSection = section.getConfigurationSection("Scale");
        if (!scaleSection.isDouble("X"))
            throw new IllegalArgumentException("Scale.X is not valid");
        if (!scaleSection.isDouble("Y"))
            throw new IllegalArgumentException("Scale.Y is not valid");
        if (!scaleSection.isDouble("Z"))
            throw new IllegalArgumentException("Scale.Z is not valid");
        ConfigurationSection translationSection = section.getConfigurationSection("Translation");
        if (!translationSection.isDouble("X"))
            throw new IllegalArgumentException("Translation.X is not valid");
        if (!translationSection.isDouble("Y"))
            throw new IllegalArgumentException("Translation.Y is not valid");
        if (!translationSection.isDouble("Z"))
            throw new IllegalArgumentException("Translation.Z is not valid");
        ConfigurationSection leftRotationSection = section.getConfigurationSection("Left-Rotation");
        if (!leftRotationSection.isDouble("X"))
            throw new IllegalArgumentException("Left-Rotation.X is not valid");
        if (!leftRotationSection.isDouble("Y"))
            throw new IllegalArgumentException("Left-Rotation.Y is not valid");
        if (!leftRotationSection.isDouble("Z"))
            throw new IllegalArgumentException("Left-Rotation.Z is not valid");
        if (!leftRotationSection.isDouble("W"))
            throw new IllegalArgumentException("Left-Rotation.W is not valid");
        ConfigurationSection rightRotationSection = section.getConfigurationSection("Right-Rotation");
        if (!rightRotationSection.isDouble("X"))
            throw new IllegalArgumentException("Right-Rotation.X is not valid");
        if (!rightRotationSection.isDouble("Y"))
            throw new IllegalArgumentException("Right-Rotation.Y is not valid");
        if (!rightRotationSection.isDouble("Z"))
            throw new IllegalArgumentException("Right-Rotation.Z is not valid");
        if (!rightRotationSection.isDouble("W"))
            throw new IllegalArgumentException("Right-Rotation.W is not valid");
        Vector3f scale = new Vector3f((float) scaleSection.getDouble("X"), (float) scaleSection.getDouble("Y"), (float) scaleSection.getDouble("Z"));
        Vector3f translation = new Vector3f((float) translationSection.getDouble("X"), (float) translationSection.getDouble("Y"), (float) translationSection.getDouble("Z"));
        Quaternionf leftRotation = new Quaternionf(leftRotationSection.getDouble("X"), leftRotationSection.getDouble("Y"), leftRotationSection.getDouble("Z"), leftRotationSection.getDouble("W"));
        Quaternionf rightRotation = new Quaternionf(rightRotationSection.getDouble("X"), rightRotationSection.getDouble("Y"), rightRotationSection.getDouble("Z"), rightRotationSection.getDouble("W"));
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
    public static Transformation TRANSFORMATION_NULL(ConfigurationSection section) {
        if (!section.isConfigurationSection("Scale"))
            return null;
        if (!section.isConfigurationSection("Translation"))
            return null;
        if (!section.isConfigurationSection("Left-Rotation"))
            return null;
        if (!section.isConfigurationSection("Right-Rotation"))
            return null;
        ConfigurationSection scaleSection = section.getConfigurationSection("Scale");
        if (!scaleSection.isDouble("X"))
            return null;
        if (!scaleSection.isDouble("Y"))
            return null;
        if (!scaleSection.isDouble("Z"))
            return null;
        ConfigurationSection translationSection = section.getConfigurationSection("Translation");
        if (!translationSection.isDouble("X"))
            return null;
        if (!translationSection.isDouble("Y"))
            return null;
        if (!translationSection.isDouble("Z"))
            return null;
        ConfigurationSection leftRotationSection = section.getConfigurationSection("Left-Rotation");
        if (!leftRotationSection.isDouble("X"))
            return null;
        if (!leftRotationSection.isDouble("Y"))
            return null;
        if (!leftRotationSection.isDouble("Z"))
            return null;
        if (!leftRotationSection.isDouble("W"))
            return null;
        ConfigurationSection rightRotationSection = section.getConfigurationSection("Right-Rotation");
        if (!rightRotationSection.isDouble("X"))
            return null;
        if (!rightRotationSection.isDouble("Y"))
            return null;
        if (!rightRotationSection.isDouble("Z"))
            return null;
        if (!rightRotationSection.isDouble("W"))
            return null;
        Vector3f scale = new Vector3f((float) scaleSection.getDouble("X"), (float) scaleSection.getDouble("Y"), (float) scaleSection.getDouble("Z"));
        Vector3f translation = new Vector3f((float) translationSection.getDouble("X"), (float) translationSection.getDouble("Y"), (float) translationSection.getDouble("Z"));
        Quaternionf leftRotation = new Quaternionf(leftRotationSection.getDouble("X"), leftRotationSection.getDouble("Y"), leftRotationSection.getDouble("Z"), leftRotationSection.getDouble("W"));
        Quaternionf rightRotation = new Quaternionf(rightRotationSection.getDouble("X"), rightRotationSection.getDouble("Y"), rightRotationSection.getDouble("Z"), rightRotationSection.getDouble("W"));
        return new Transformation(translation, leftRotation, scale, rightRotation);
    }
}
