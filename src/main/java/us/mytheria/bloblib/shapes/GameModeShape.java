package us.mytheria.bloblib.shapes;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameModeShape {

    public static <U> HashMap<String, U> toStringKeys(Map<GameMode, U> map) {
        HashMap<String, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.serialize(key), value));
        return newMap;
    }

    public static <U> HashMap<GameMode, U> toGameModeKeys(Map<String, U> map) {
        HashMap<GameMode, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeGameMode(key), value));
        return newMap;
    }

    public static void serializeGameModeListMap(Map<String, List<GameMode>> map, ConfigurationSection section, String path) {
        Map<String, List<String>> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            List<String> stringList = new ArrayList<>();
            value.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
            stringMap.put(key, stringList);
        });
        stringMap.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, List<GameMode>> deserializeGameModeListMap(ConfigurationSection section, String path) {
        HashMap<String, List<GameMode>> hashMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            List<GameMode> list = new ArrayList<>();
            section.getStringList(path + "." + key).forEach(string -> {
                list.add(SerializationLib.deserializeGameMode(string));
            });
            hashMap.put(key, list);
        });
        return hashMap;
    }

    public static void serializeGameModeList(List<GameMode> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(GameMode -> stringList.add(SerializationLib.serialize(GameMode)));
        section.set(path, stringList);
    }

    public static List<GameMode> deserializeGameModeList(ConfigurationSection section, String path) {
        List<GameMode> GameModeList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                GameModeList.add(SerializationLib.deserializeGameMode(string)));
        return GameModeList;
    }

    public static void serializeGameModeMap(Map<String, GameMode> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, GameMode> deserializeGameModeMap(ConfigurationSection section, String path) {
        HashMap<String, GameMode> GameModeMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            GameModeMap.put(key, SerializationLib.deserializeGameMode(string));
        });
        return GameModeMap;
    }
}
