package us.mytheria.bloblib.shapes;

import org.bukkit.WeatherType;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherTypeShape {

    public static <U> HashMap<String, U> toStringKeys(Map<WeatherType, U> map) {
        HashMap<String, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.serialize(key), value));
        return newMap;
    }

    public static <U> HashMap<WeatherType, U> toWeatherTypeKeys(Map<String, U> map) {
        HashMap<WeatherType, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeWeatherType(key), value));
        return newMap;
    }

    public static void serializeWeatherTypeListMap(Map<String, List<WeatherType>> map, ConfigurationSection section, String path) {
        Map<String, List<String>> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            List<String> stringList = new ArrayList<>();
            value.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
            stringMap.put(key, stringList);
        });
        stringMap.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, List<WeatherType>> deserializeWeatherTypeListMap(ConfigurationSection section, String path) {
        HashMap<String, List<WeatherType>> hashMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            List<WeatherType> list = new ArrayList<>();
            section.getStringList(path + "." + key).forEach(string -> {
                list.add(SerializationLib.deserializeWeatherType(string));
            });
            hashMap.put(key, list);
        });
        return hashMap;
    }

    public static void serializeWeatherTypeList(List<WeatherType> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(WeatherType -> stringList.add(SerializationLib.serialize(WeatherType)));
        section.set(path, stringList);
    }

    public static List<WeatherType> deserializeWeatherTypeList(ConfigurationSection section, String path) {
        List<WeatherType> WeatherTypeList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                WeatherTypeList.add(SerializationLib.deserializeWeatherType(string)));
        return WeatherTypeList;
    }

    public static void serializeWeatherTypeMap(Map<String, WeatherType> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, WeatherType> deserializeWeatherTypeMap(ConfigurationSection section, String path) {
        HashMap<String, WeatherType> WeatherTypeMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            WeatherTypeMap.put(key, SerializationLib.deserializeWeatherType(string));
        });
        return WeatherTypeMap;
    }
}
