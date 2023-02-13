package us.mytheria.bloblib.shapes;

import org.bukkit.Difficulty;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DifficultyShape {

    public static <U> HashMap<String, U> difficultyToStringKeys(Map<Difficulty, U> map) {
        HashMap<String, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.serialize(key), value));
        return newMap;
    }

    public static void serializeDifficultyListMap(Map<String, List<Difficulty>> map, ConfigurationSection section, String path) {
        Map<String, List<String>> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            List<String> stringList = new ArrayList<>();
            value.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
            stringMap.put(key, stringList);
        });
        stringMap.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, List<Difficulty>> deserializeDifficultyListMap(ConfigurationSection section, String path) {
        HashMap<String, List<Difficulty>> hashMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            List<Difficulty> list = new ArrayList<>();
            section.getStringList(path + "." + key).forEach(string -> {
                list.add(SerializationLib.deserializeDifficulty(string));
            });
            hashMap.put(key, list);
        });
        return hashMap;
    }

    public static void serializeDifficultyList(List<Difficulty> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(Difficulty -> stringList.add(SerializationLib.serialize(Difficulty)));
        section.set(path, stringList);
    }

    public static List<Difficulty> deserializeDifficultyList(ConfigurationSection section, String path) {
        List<Difficulty> DifficultyList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                DifficultyList.add(SerializationLib.deserializeDifficulty(string)));
        return DifficultyList;
    }

    public static void serializeDifficultyMap(Map<String, Difficulty> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, Difficulty> deserializeDifficultyMap(ConfigurationSection section, String path) {
        HashMap<String, Difficulty> DifficultyMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            DifficultyMap.put(key, SerializationLib.deserializeDifficulty(string));
        });
        return DifficultyMap;
    }
}
