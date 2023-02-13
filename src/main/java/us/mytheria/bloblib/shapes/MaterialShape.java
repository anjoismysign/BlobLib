package us.mytheria.bloblib.shapes;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialShape {

    public static <U> HashMap<String, U> materialToStringKeys(Map<Material, U> map) {
        HashMap<String, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.serialize(key), value));
        return newMap;
    }

    public static void serializeMaterialListMap(Map<String, List<Material>> map, ConfigurationSection section, String path) {
        Map<String, List<String>> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            List<String> stringList = new ArrayList<>();
            value.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
            stringMap.put(key, stringList);
        });
        stringMap.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, List<Material>> deserializeMaterialListMap(ConfigurationSection section, String path) {
        HashMap<String, List<Material>> hashMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            List<Material> list = new ArrayList<>();
            section.getStringList(path + "." + key).forEach(string -> {
                list.add(SerializationLib.deserializeMaterial(string));
            });
            hashMap.put(key, list);
        });
        return hashMap;
    }

    public static void serializeMaterialList(List<Material> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(Material -> stringList.add(SerializationLib.serialize(Material)));
        section.set(path, stringList);
    }

    public static List<Material> deserializeMaterialList(ConfigurationSection section, String path) {
        List<Material> MaterialList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                MaterialList.add(SerializationLib.deserializeMaterial(string)));
        return MaterialList;
    }

    public static void serializeMaterialMap(Map<String, Material> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, Material> deserializeMaterialMap(ConfigurationSection section, String path) {
        HashMap<String, Material> MaterialMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            MaterialMap.put(key, SerializationLib.deserializeMaterial(string));
        });
        return MaterialMap;
    }
}
