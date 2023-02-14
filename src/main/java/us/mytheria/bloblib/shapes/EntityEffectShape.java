package us.mytheria.bloblib.shapes;

import org.bukkit.EntityEffect;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityEffectShape {

    public static <U> HashMap<String, U> toStringKeys(Map<EntityEffect, U> map) {
        HashMap<String, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.serialize(key), value));
        return newMap;
    }

    public static <U> HashMap<EntityEffect, U> toEntityEffectKeys(Map<String, U> map) {
        HashMap<EntityEffect, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeEntityEffect(key), value));
        return newMap;
    }

    public static void serializeEntityEffectListMap(Map<String, List<EntityEffect>> map, ConfigurationSection section, String path) {
        Map<String, List<String>> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            List<String> stringList = new ArrayList<>();
            value.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
            stringMap.put(key, stringList);
        });
        stringMap.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, List<EntityEffect>> deserializeEntityEffectListMap(ConfigurationSection section, String path) {
        HashMap<String, List<EntityEffect>> hashMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            List<EntityEffect> list = new ArrayList<>();
            section.getStringList(path + "." + key).forEach(string -> {
                list.add(SerializationLib.deserializeEntityEffect(string));
            });
            hashMap.put(key, list);
        });
        return hashMap;
    }

    public static void serializeEntityEffectList(List<EntityEffect> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(EntityEffect -> stringList.add(SerializationLib.serialize(EntityEffect)));
        section.set(path, stringList);
    }

    public static List<EntityEffect> deserializeEntityEffectList(ConfigurationSection section, String path) {
        List<EntityEffect> EntityEffectList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                EntityEffectList.add(SerializationLib.deserializeEntityEffect(string)));
        return EntityEffectList;
    }

    public static void serializeEntityEffectMap(Map<String, EntityEffect> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, EntityEffect> deserializeEntityEffectMap(ConfigurationSection section, String path) {
        HashMap<String, EntityEffect> EntityEffectMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            EntityEffectMap.put(key, SerializationLib.deserializeEntityEffect(string));
        });
        return EntityEffectMap;
    }
}
