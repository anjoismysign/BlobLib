package us.mytheria.bloblib.shapes;

import org.bukkit.CropState;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CropStateShape {

    public static <U> HashMap<String, U> toStringKeys(Map<CropState, U> map) {
        HashMap<String, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.serialize(key), value));
        return newMap;
    }

    public static <U> HashMap<CropState, U> toCropStateKeys(Map<String, U> map) {
        HashMap<CropState, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeCropState(key), value));
        return newMap;
    }

    public static void serializeCropStateListMap(Map<String, List<CropState>> map, ConfigurationSection section, String path) {
        Map<String, List<String>> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            List<String> stringList = new ArrayList<>();
            value.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
            stringMap.put(key, stringList);
        });
        stringMap.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, List<CropState>> deserializeCropStateListMap(ConfigurationSection section, String path) {
        HashMap<String, List<CropState>> hashMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            List<CropState> list = new ArrayList<>();
            section.getStringList(path + "." + key).forEach(string -> {
                list.add(SerializationLib.deserializeCropState(string));
            });
            hashMap.put(key, list);
        });
        return hashMap;
    }

    public static void serializeCropStateList(List<CropState> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(CropState -> stringList.add(SerializationLib.serialize(CropState)));
        section.set(path, stringList);
    }

    public static List<CropState> deserializeCropStateList(ConfigurationSection section, String path) {
        List<CropState> CropStateList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                CropStateList.add(SerializationLib.deserializeCropState(string)));
        return CropStateList;
    }

    public static void serializeCropStateMap(Map<String, CropState> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, CropState> deserializeCropStateMap(ConfigurationSection section, String path) {
        HashMap<String, CropState> CropStateMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            CropStateMap.put(key, SerializationLib.deserializeCropState(string));
        });
        return CropStateMap;
    }
}
