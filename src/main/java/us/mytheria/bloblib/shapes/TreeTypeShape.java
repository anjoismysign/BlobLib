package us.mytheria.bloblib.shapes;

import org.bukkit.TreeType;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeTypeShape {

    public static <U> HashMap<String, U> treeTypeToStringKeys(Map<TreeType, U> map) {
        HashMap<String, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.serialize(key), value));
        return newMap;
    }

    public static void serializeTreeTypeListMap(Map<String, List<TreeType>> map, ConfigurationSection section, String path) {
        Map<String, List<String>> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            List<String> stringList = new ArrayList<>();
            value.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
            stringMap.put(key, stringList);
        });
        stringMap.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, List<TreeType>> deserializeTreeTypeListMap(ConfigurationSection section, String path) {
        HashMap<String, List<TreeType>> hashMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            List<TreeType> list = new ArrayList<>();
            section.getStringList(path + "." + key).forEach(string -> {
                list.add(SerializationLib.deserializeTreeType(string));
            });
            hashMap.put(key, list);
        });
        return hashMap;
    }

    public static void serializeTreeTypeList(List<TreeType> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(TreeType -> stringList.add(SerializationLib.serialize(TreeType)));
        section.set(path, stringList);
    }

    public static List<TreeType> deserializeTreeTypeList(ConfigurationSection section, String path) {
        List<TreeType> TreeTypeList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                TreeTypeList.add(SerializationLib.deserializeTreeType(string)));
        return TreeTypeList;
    }

    public static void serializeTreeTypeMap(Map<String, TreeType> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, TreeType> deserializeTreeTypeMap(ConfigurationSection section, String path) {
        HashMap<String, TreeType> TreeTypeMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            TreeTypeMap.put(key, SerializationLib.deserializeTreeType(string));
        });
        return TreeTypeMap;
    }
}
