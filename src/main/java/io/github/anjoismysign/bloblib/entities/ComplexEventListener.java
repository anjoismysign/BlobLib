package io.github.anjoismysign.bloblib.entities;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ComplexEventListener {
    private final Map<String, Object> configValues;
    private final boolean register;

    public static ComplexEventListener of(File file) {
        return new ComplexEventListener(YamlConfiguration.loadConfiguration(file));
    }

    public ComplexEventListener(ConfigurationSection section) {
        configValues = new HashMap<>();
        if (!section.isBoolean("Register"))
            throw new IllegalArgumentException("'Register' must be a boolean");
        register = section.getBoolean("Register");
        ConfigurationSection configSection = section.getConfigurationSection("Values");
        Set<String> keys = configSection.getKeys(true);
        for (String key : keys) {
            if (configSection.isConfigurationSection(key)) {
                configValues.put(key, configSection.getConfigurationSection(key));
                continue;
            }
            Object value = configSection.get(key);
            configValues.put(key, value);
        }
    }

    public boolean register() {
        return register;
    }

    public boolean ifRegister(Consumer<ComplexEventListener> consumer) {
        if (register) {
            consumer.accept(this);
            return true;
        }
        return false;
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
        try {
            return (double) getValue(key);
        } catch (ClassCastException e) {
            return Double.valueOf((Float) getValue(key));
        }
    }

    public long getLong(String key) {
        try {
            return (long) getValue(key);
        } catch (ClassCastException e) {
            return Long.valueOf((Integer) getValue(key));
        }
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

    public ConfigurationSection getConfigurationSection(String key) {
        return (ConfigurationSection) getValue(key);
    }
}