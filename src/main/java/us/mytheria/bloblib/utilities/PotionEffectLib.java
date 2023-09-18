package us.mytheria.bloblib.utilities;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectLib {

    public static void serialize(ConfigurationSection section, PotionEffect effect) {
        section.set("Type", effect.getType().getName());
        section.set("Amplifier", effect.getAmplifier());
        section.set("Duration", effect.getDuration());
        section.set("Ambient", effect.isAmbient());
        section.set("Particles", effect.hasParticles());
        section.set("Icon", effect.hasIcon());
    }

    public static PotionEffect deserialize(ConfigurationSection section) {
        if (!section.isString("Type"))
            return null;
        PotionEffectType type;
        try {
            type = PotionEffectType.getByName(section.getString("Type"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid potion effect type in '" + section.getName() + "': " + section.getString("Type"));
        }
        if (!section.isInt("Amplifier"))
            throw new IllegalArgumentException("Invalid amplifier in '" + section.getName() + "': " + section.getString("Amplifier"));
        if (!section.isInt("Duration"))
            throw new IllegalArgumentException("Invalid duration in '" + section.getName() + "': " + section.getString("Duration"));
        if (!section.isBoolean("Ambient"))
            throw new IllegalArgumentException("Invalid ambient in '" + section.getName() + "': " + section.getString("Ambient"));
        if (!section.isBoolean("Particles"))
            throw new IllegalArgumentException("Invalid particles in '" + section.getName() + "': " + section.getString("Particles"));
        if (!section.isBoolean("Icon"))
            throw new IllegalArgumentException("Invalid icon in '" + section.getName() + "': " + section.getString("Icon"));
        int amplifier = section.getInt("Amplifier");
        int duration = section.getInt("Duration");
        boolean ambient = section.getBoolean("Ambient");
        boolean particles = section.getBoolean("Particles");
        boolean icon = section.getBoolean("Icon");
        return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
    }

    public static String stringSerialize(PotionEffect effect) {
        return effect.getType().getName() + " " + effect.getAmplifier() + " " + effect.getDuration() + " " + effect.isAmbient() + " " + effect.hasParticles() + " " + effect.hasIcon();
    }

    public static PotionEffect deserializeString(String string) {
        String[] split = string.split(" ");
        if (split.length != 6)
            throw new IllegalArgumentException("Invalid potion effect string: " + string);
        PotionEffectType type;
        try {
            type = PotionEffectType.getByName(split[0]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid potion effect type in '" + string + "': " + split[0]);
        }
        int amplifier;
        try {
            amplifier = Integer.parseInt(split[1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid amplifier in '" + string + "': " + split[1]);
        }
        int duration;
        try {
            duration = Integer.parseInt(split[2]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid duration in '" + string + "': " + split[2]);
        }
        boolean ambient;
        try {
            ambient = Boolean.parseBoolean(split[3]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid ambient in '" + string + "': " + split[3]);
        }
        boolean particles;
        try {
            particles = Boolean.parseBoolean(split[4]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid particles in '" + string + "': " + split[4]);
        }
        boolean icon;
        try {
            icon = Boolean.parseBoolean(split[5]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid icon in '" + string + "': " + split[5]);
        }
        return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
    }
}
