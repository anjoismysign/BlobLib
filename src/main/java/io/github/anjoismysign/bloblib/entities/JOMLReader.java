package io.github.anjoismysign.bloblib.entities;

import org.bukkit.configuration.ConfigurationSection;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class JOMLReader {

    public static Vector3f READ_VECTOR3F(ConfigurationSection section, String name) {
        if (!section.isConfigurationSection(name))
            throw new IllegalArgumentException(name + " is not valid");
        ConfigurationSection vector = section.getConfigurationSection(name);
        if (!vector.isDouble("X"))
            throw new IllegalArgumentException(name + ".X is not valid");
        if (!vector.isDouble("Y"))
            throw new IllegalArgumentException(name + ".Y is not valid");
        if (!vector.isDouble("Z"))
            throw new IllegalArgumentException(name + ".Z is not valid");
        return new Vector3f(
                (float) vector.getDouble("X"),
                (float) vector.getDouble("Y"),
                (float) vector.getDouble("Z"));
    }

    public static Quaternionf READ_QUATERNIONF(ConfigurationSection section, String name) {
        if (!section.isConfigurationSection(name) && !section.isConfigurationSection(name + "-Quaternion"))
            throw new IllegalArgumentException(name + " is not valid");
        Quaternionf quaternionf;
        if (section.isConfigurationSection(name)) {
            ConfigurationSection angle = section.getConfigurationSection(name);
            if (!angle.isDouble("X"))
                throw new IllegalArgumentException(name + ".X is not valid");
            if (!angle.isDouble("Y"))
                throw new IllegalArgumentException(name + ".Y is not valid");
            if (!angle.isDouble("Z"))
                throw new IllegalArgumentException(name + ".Z is not valid");
            if (!angle.isDouble("Angle"))
                throw new IllegalArgumentException(name + ".Angle is not valid");
            AxisAngle4f angle4f = new AxisAngle4f(
                    (float) angle.getDouble("Angle"),
                    (float) angle.getDouble("X"),
                    (float) angle.getDouble("Y"),
                    (float) angle.getDouble("Z")
            );
            quaternionf = new Quaternionf(angle4f);
        } else {
            ConfigurationSection quaternion = section.getConfigurationSection(name + "-Quaternion");
            if (!quaternion.isDouble("X"))
                throw new IllegalArgumentException(name + "-Quaternion.X is not valid");
            if (!quaternion.isDouble("Y"))
                throw new IllegalArgumentException(name + "-Quaternion.Y is not valid");
            if (!quaternion.isDouble("Z"))
                throw new IllegalArgumentException(name + "-Quaternion.Z is not valid");
            if (!quaternion.isDouble("W"))
                throw new IllegalArgumentException(name + "-Quaternion.W is not valid");
            quaternionf = new Quaternionf(
                    quaternion.getDouble("X"),
                    quaternion.getDouble("Y"),
                    quaternion.getDouble("Z"),
                    quaternion.getDouble("W"));
        }
        return quaternionf;
    }
}
