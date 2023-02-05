package us.mytheria.bloblib.entities;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

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

    public static SimpleEventListener<List<Byte>> BYTE_LIST(ConfigurationSection section, String path) {
        boolean register = section.getBoolean("Register");
        List<Byte> value = section.getByteList(path);
        return new SimpleEventListener<>(register, value);
    }

    public static SimpleEventListener<List<Short>> SHORT_LIST(ConfigurationSection section, String path) {
        boolean register = section.getBoolean("Register");
        List<Short> value = section.getShortList(path);
        return new SimpleEventListener<>(register, value);
    }

    public static SimpleEventListener<List<Integer>> INTEGER_LIST(ConfigurationSection section, String path) {
        boolean register = section.getBoolean("Register");
        List<Integer> value = section.getIntegerList(path);
        return new SimpleEventListener<>(register, value);
    }

    public static SimpleEventListener<List<Long>> LONG_LIST(ConfigurationSection section, String path) {
        boolean register = section.getBoolean("Register");
        List<Long> value = section.getLongList(path);
        return new SimpleEventListener<>(register, value);
    }

    public static SimpleEventListener<List<Float>> FLOAT_LIST(ConfigurationSection section, String path) {
        boolean register = section.getBoolean("Register");
        List<Float> value = section.getFloatList(path);
        return new SimpleEventListener<>(register, value);
    }

    public static SimpleEventListener<List<Double>> DOUBLE_LIST(ConfigurationSection section, String path) {
        boolean register = section.getBoolean("Register");
        List<Double> value = section.getDoubleList(path);
        return new SimpleEventListener<>(register, value);
    }

    public static SimpleEventListener<List<Boolean>> BOOLEAN_LIST(ConfigurationSection section, String path) {
        boolean register = section.getBoolean("Register");
        List<Boolean> value = section.getBooleanList(path);
        return new SimpleEventListener<>(register, value);
    }

    public static SimpleEventListener<List<Character>> CHARACTER_LIST(ConfigurationSection section, String path) {
        boolean register = section.getBoolean("Register");
        List<Character> value = section.getCharacterList(path);
        return new SimpleEventListener<>(register, value);
    }

    public static SimpleEventListener<List<String>> STRING_LIST(ConfigurationSection section, String path) {
        boolean register = section.getBoolean("Register");
        List<String> value = section.getStringList(path);
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