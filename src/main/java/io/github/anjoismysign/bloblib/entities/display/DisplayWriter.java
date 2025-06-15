package io.github.anjoismysign.bloblib.entities.display;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;

public class DisplayWriter {
    public static void WRITE(ConfigurationSection section,
                             Display.Brightness brightness,
                             boolean applyBrightness) {
        section.set("Apply-Brightness", applyBrightness);
        section.set("Block-Light", brightness.getBlockLight());
        section.set("Sky-Light", brightness.getSkyLight());
    }

    public static void WRITE(ConfigurationSection section,
                             Display.Billboard billboard) {
        section.set("Billboard", billboard.name());
    }

    public static void WRITE(ConfigurationSection section,
                             Transformation transformation,
                             boolean useQuaternion) {
        ConfigurationSection scaleSection = section.createSection("Scale");
        scaleSection.set("X", transformation.getScale().x);
        scaleSection.set("Y", transformation.getScale().y);
        scaleSection.set("Z", transformation.getScale().z);
        ConfigurationSection translationSection = section.createSection("Translation");
        translationSection.set("X", transformation.getTranslation().x);
        translationSection.set("Y", transformation.getTranslation().y);
        translationSection.set("Z", transformation.getTranslation().z);
        if (useQuaternion) {
            ConfigurationSection leftRotationSection = section.createSection("Left-Rotation-Quaternion");
            leftRotationSection.set("X", transformation.getLeftRotation().x);
            leftRotationSection.set("Y", transformation.getLeftRotation().y);
            leftRotationSection.set("Z", transformation.getLeftRotation().z);
            leftRotationSection.set("W", transformation.getLeftRotation().w);
            ConfigurationSection rightRotationSection = section.createSection("Right-Rotation-Quaternion");
            rightRotationSection.set("X", transformation.getRightRotation().x);
            rightRotationSection.set("Y", transformation.getRightRotation().y);
            rightRotationSection.set("Z", transformation.getRightRotation().z);
            rightRotationSection.set("W", transformation.getRightRotation().w);
        } else {
            AxisAngle4f leftRotation = new AxisAngle4f(transformation.getLeftRotation());
            ConfigurationSection leftRotationSection = section.createSection("Left-Rotation");
            leftRotationSection.set("X", leftRotation.x);
            leftRotationSection.set("Y", leftRotation.y);
            leftRotationSection.set("Z", leftRotation.z);
            leftRotationSection.set("Angle", leftRotation.angle);
            AxisAngle4f rightRotation = new AxisAngle4f(transformation.getRightRotation());
            ConfigurationSection rightRotationSection = section.createSection("Right-Rotation");
            rightRotationSection.set("X", rightRotation.x);
            rightRotationSection.set("Y", rightRotation.y);
            rightRotationSection.set("Z", rightRotation.z);
            rightRotationSection.set("Angle", rightRotation.angle);
        }
    }

    public static void WRITE(ConfigurationSection section,
                             Transformation transformation) {
        WRITE(section, transformation, true);
    }
}
