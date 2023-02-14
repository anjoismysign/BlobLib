package us.mytheria.bloblib.shapes;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityTypeShape {

    public static <U> HashMap<String, U> toStringKeys(Map<EntityType, U> map) {
        HashMap<String, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.serialize(key), value));
        return newMap;
    }

    public static <U> HashMap<EntityType, U> toEntityTypeKeys(Map<String, U> map) {
        HashMap<EntityType, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeEntityType(key), value));
        return newMap;
    }

    public static void serializeEntityTypeListMap(Map<String, List<EntityType>> map, ConfigurationSection section, String path) {
        Map<String, List<String>> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            List<String> stringList = new ArrayList<>();
            value.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
            stringMap.put(key, stringList);
        });
        stringMap.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, List<EntityType>> deserializeEntityTypeListMap(ConfigurationSection section, String path) {
        HashMap<String, List<EntityType>> hashMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            List<EntityType> list = new ArrayList<>();
            section.getStringList(path + "." + key).forEach(string -> {
                list.add(SerializationLib.deserializeEntityType(string));
            });
            hashMap.put(key, list);
        });
        return hashMap;
    }

    public static void serializeEntityTypeList(List<EntityType> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(EntityType -> stringList.add(SerializationLib.serialize(EntityType)));
        section.set(path, stringList);
    }

    public static List<EntityType> deserializeEntityTypeList(ConfigurationSection section, String path) {
        List<EntityType> EntityTypeList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                EntityTypeList.add(SerializationLib.deserializeEntityType(string)));
        return EntityTypeList;
    }

    public static void serializeEntityTypeMap(Map<String, EntityType> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, EntityType> deserializeEntityTypeMap(ConfigurationSection section, String path) {
        HashMap<String, EntityType> EntityTypeMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            EntityTypeMap.put(key, SerializationLib.deserializeEntityType(string));
        });
        return EntityTypeMap;
    }
}
