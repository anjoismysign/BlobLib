package us.mytheria.bloblib.entities.display;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;

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
                             Transformation transformation) {
        ConfigurationSection scaleSection = section.createSection("Scale");
        scaleSection.set("X", transformation.getScale().x);
        scaleSection.set("Y", transformation.getScale().y);
        scaleSection.set("Z", transformation.getScale().z);
        ConfigurationSection translationSection = section.createSection("Translation");
        translationSection.set("X", transformation.getTranslation().x);
        translationSection.set("Y", transformation.getTranslation().y);
        translationSection.set("Z", transformation.getTranslation().z);
        ConfigurationSection leftRotationSection = section.createSection("Left-Rotation");
        leftRotationSection.set("X", transformation.getLeftRotation().x);
        leftRotationSection.set("Y", transformation.getLeftRotation().y);
        leftRotationSection.set("Z", transformation.getLeftRotation().z);
        leftRotationSection.set("W", transformation.getLeftRotation().w);
        ConfigurationSection rightRotationSection = section.createSection("Right-Rotation");
        rightRotationSection.set("X", transformation.getRightRotation().x);
        rightRotationSection.set("Y", transformation.getRightRotation().y);
        rightRotationSection.set("Z", transformation.getRightRotation().z);
        rightRotationSection.set("W", transformation.getRightRotation().w);
    }
}
