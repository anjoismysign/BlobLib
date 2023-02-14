package us.mytheria.bloblib.shapes;

import org.bukkit.Effect;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffectShape {

    public static <U> HashMap<String, U> toStringKeys(Map<Effect, U> map) {
        HashMap<String, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.serialize(key), value));
        return newMap;
    }

    public static <U> HashMap<Effect, U> toEffectKeys(Map<String, U> map) {
        HashMap<Effect, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeEffect(key), value));
        return newMap;
    }

    public static void serializeEffectListMap(Map<String, List<Effect>> map, ConfigurationSection section, String path) {
        Map<String, List<String>> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            List<String> stringList = new ArrayList<>();
            value.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
            stringMap.put(key, stringList);
        });
        stringMap.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, List<Effect>> deserializeEffectListMap(ConfigurationSection section, String path) {
        HashMap<String, List<Effect>> hashMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            List<Effect> list = new ArrayList<>();
            section.getStringList(path + "." + key).forEach(string -> {
                list.add(SerializationLib.deserializeEffect(string));
            });
            hashMap.put(key, list);
        });
        return hashMap;
    }

    public static void serializeEffectList(List<Effect> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(Effect -> stringList.add(SerializationLib.serialize(Effect)));
        section.set(path, stringList);
    }

    public static List<Effect> deserializeEffectList(ConfigurationSection section, String path) {
        List<Effect> EffectList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                EffectList.add(SerializationLib.deserializeEffect(string)));
        return EffectList;
    }

    public static void serializeEffectMap(Map<String, Effect> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, Effect> deserializeEffectMap(ConfigurationSection section, String path) {
        HashMap<String, Effect> EffectMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            EffectMap.put(key, SerializationLib.deserializeEffect(string));
        });
        return EffectMap;
    }
}
