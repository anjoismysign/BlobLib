package us.mytheria.bloblib.shapes;

import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DyeColorShape {

    public static <U> HashMap<String, U> dyeColorToStringKeys(Map<DyeColor, U> map) {
        HashMap<String, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.serialize(key), value));
        return newMap;
    }

    public static void serializeDyeColorListMap(Map<String, List<DyeColor>> map, ConfigurationSection section, String path) {
        Map<String, List<String>> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            List<String> stringList = new ArrayList<>();
            value.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
            stringMap.put(key, stringList);
        });
        stringMap.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, List<DyeColor>> deserializeDyeColorListMap(ConfigurationSection section, String path) {
        HashMap<String, List<DyeColor>> hashMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            List<DyeColor> list = new ArrayList<>();
            section.getStringList(path + "." + key).forEach(string -> {
                list.add(SerializationLib.deserializeDyeColor(string));
            });
            hashMap.put(key, list);
        });
        return hashMap;
    }

    public static void serializeDyeColorList(List<DyeColor> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(DyeColor -> stringList.add(SerializationLib.serialize(DyeColor)));
        section.set(path, stringList);
    }

    public static List<DyeColor> deserializeDyeColorList(ConfigurationSection section, String path) {
        List<DyeColor> DyeColorList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                DyeColorList.add(SerializationLib.deserializeDyeColor(string)));
        return DyeColorList;
    }

    public static void serializeDyeColorMap(Map<String, DyeColor> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, DyeColor> deserializeDyeColorMap(ConfigurationSection section, String path) {
        HashMap<String, DyeColor> DyeColorMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            DyeColorMap.put(key, SerializationLib.deserializeDyeColor(string));
        });
        return DyeColorMap;
    }
}
