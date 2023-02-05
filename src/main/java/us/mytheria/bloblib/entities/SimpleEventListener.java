package us.mytheria.bloblib.entities;

import org.bukkit.configuration.ConfigurationSection;

public record SimpleEventListener<T>(boolean register, T value) {

    public static <T> SimpleEventListener<T> TRUE(T value) {
        return new SimpleEventListener<>(true, value);
    }

    public static <T> SimpleEventListener<T> FALSE(T value) {
        return new SimpleEventListener<>(false, value);
    }

    public static SimpleEventListener<Double> DOUBLE(ConfigurationSection section, String path) {
        boolean register = section.getBoolean("Register");
        double value = section.getDouble(path);
        return new SimpleEventListener<>(register, value);
    }

    public static SimpleEventListener<Integer> INTEGER(ConfigurationSection section, String path) {
        boolean register = section.getBoolean("Register");
        int value = section.getInt(path);
        return new SimpleEventListener<>(register, value);
    }

    public static SimpleEventListener<Boolean> BOOLEAN(ConfigurationSection section, String path) {
        boolean register = section.getBoolean("Register");
        boolean value = section.getBoolean(path);
        return new SimpleEventListener<>(register, value);
    }

    public static SimpleEventListener<String> STRING(ConfigurationSection section, String path) {
        boolean register = section.getBoolean("Register");
        String value = section.getString(path);
        return new SimpleEventListener<>(register, value);
    }

    public static SimpleEventListener<Long> LONG(ConfigurationSection section, String path) {
        boolean register = section.getBoolean("Register");
        long value = section.getLong(path);
        return new SimpleEventListener<>(register, value);
    }

    /**
     * Writes the SimpleEventListener to the given ConfigurationSection
     * <p>
     * An example. If I would like to write it for an event named
     * "EntityKill" passing "reward" as path, the output for TRUE(9.99) would be:
     * <pre>
     *     EntityKill:
     *       Register: true
     *       9.99
     *     </pre>
     *
     * @param section The ConfigurationSection to write to
     * @param path    The path of the value
     */
    public void write(ConfigurationSection section, String path) {
        section.set("Register", register);
        section.set(path, value);
    }
}