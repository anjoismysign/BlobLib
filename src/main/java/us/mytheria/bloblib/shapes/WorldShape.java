package us.mytheria.bloblib.shapes;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldShape {

    public static <U> HashMap<String, U> toStringKeys(Map<World, U> map) {
        HashMap<String, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.serialize(key), value));
        return newMap;
    }

    public static <U> HashMap<World, U> toWorldKeys(Map<String, U> map) {
        HashMap<World, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeWorld(key), value));
        return newMap;
    }

    /**
     * Will convert a Map(String, List(World)) to Map(String, List(String))
     *
     * @param map     The map to convert
     * @param section The section to save the map to
     * @param path    The path to save the map to
     */
    public static void serializeWorldListMap(Map<String, List<World>> map, ConfigurationSection section, String path) {
        Map<String, List<String>> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            List<String> stringList = new ArrayList<>();
            value.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
            stringMap.put(key, stringList);
        });
        stringMap.forEach((key, value) -> section.set(path + "." + key, value));
    }

    /**
     * Will convert a Map(String, List(String)) to Map(String, List(World))
     *
     * @param section The section to load the map from
     * @param path    The path to load the map from
     * @return The converted map
     */
    public static HashMap<String, List<World>> deserializeWorldListMap(ConfigurationSection section, String path) {
        HashMap<String, List<World>> hashMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            List<World> list = new ArrayList<>();
            section.getStringList(path + "." + key).forEach(string -> {
                list.add(SerializationLib.deserializeWorld(string));
            });
            hashMap.put(key, list);
        });
        return hashMap;
    }

    public static void serializeWorldList(List<World> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(World -> stringList.add(SerializationLib.serialize(World)));
        section.set(path, stringList);
    }

    public static List<World> deserializeWorldList(ConfigurationSection section, String path) {
        List<World> WorldList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                WorldList.add(SerializationLib.deserializeWorld(string)));
        return WorldList;
    }

    public static void serializeWorldMap(Map<String, World> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, World> deserializeWorldMap(ConfigurationSection section, String path) {
        HashMap<String, World> WorldMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            WorldMap.put(key, SerializationLib.deserializeWorld(string));
        });
        return WorldMap;
    }
}
