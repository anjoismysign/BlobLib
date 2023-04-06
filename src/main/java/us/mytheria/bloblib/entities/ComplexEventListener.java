package us.mytheria.bloblib.entities;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class ComplexEventListener {
    private final File file;
    private Map<String, Object> configValues;
    private boolean register;

    public ComplexEventListener(File file) {
        this.file = file;
        reload();
    }

    public void reload() {
        configValues = new HashMap<>();
        update();
    }

    private void update() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.isBoolean("Register"))
            throw new IllegalArgumentException("'Register' must be a boolean");
        boolean register = config.getBoolean("Register");
        ConfigurationSection configSection = config.getConfigurationSection("Values");
        Set<String> keys = configSection.getKeys(false);
        for (String key : keys) {
            Object value = configSection.get(key);
            configValues.put(key, value);
        }
    }

    public boolean register() {
        return register;
    }

    private Object getValue(String key) {
        return configValues.get(key);
    }

    public int getInt(String key) {
        return (int) getValue(key);
    }

    public boolean getBoolean(String key) {
        return (boolean) getValue(key);
    }

    public double getDouble(String key) {
        return (double) getValue(key);
    }

    public long getLong(String key) {
        return (long) getValue(key);
    }

    public float getFloat(String key) {
        return (float) getValue(key);
    }

    public String getString(String key) {
        return (String) getValue(key);
    }

    public List<Integer> getIntList(String key) {
        List<Integer> intList = new ArrayList<>();
        List<?> list = (List<?>) getValue(key);
        for (Object item : list) {
            intList.add((int) item);
        }
        return intList;
    }

    public List<Boolean> getBooleanList(String key) {
        List<Boolean> booleanList = new ArrayList<>();
        List<?> list = (List<?>) getValue(key);
        for (Object item : list) {
            booleanList.add((boolean) item);
        }
        return booleanList;
    }

    public List<Double> getDoubleList(String key) {
        List<Double> doubleList = new ArrayList<>();
        List<?> list = (List<?>) getValue(key);
        for (Object item : list) {
            doubleList.add((double) item);
        }
        return doubleList;
    }

    public List<Long> getLongList(String key) {
        List<Long> longList = new ArrayList<>();
        List<?> list = (List<?>) getValue(key);
        for (Object item : list) {
            longList.add((long) item);
        }
        return longList;
    }

    public List<Float> getFloatList(String key) {
        List<Float> floatList = new ArrayList<>();
        List<?> list = (List<?>) getValue(key);
        for (Object item : list) {
            floatList.add((float) item);
        }
        return floatList;
    }

    @SuppressWarnings("unchecked")
    public List<String> getStringList(String key) {
        return (List<String>) getValue(key);
    }
}