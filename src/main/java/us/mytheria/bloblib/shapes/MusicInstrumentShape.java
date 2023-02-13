package us.mytheria.bloblib.shapes;

import org.bukkit.MusicInstrument;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicInstrumentShape {

    public static <U> HashMap<String, U> musicInstrumentToStringKeys(Map<MusicInstrument, U> map) {
        HashMap<String, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.serialize(key), value));
        return newMap;
    }

    public static void serializeMusicInstrumentListMap(Map<String, List<MusicInstrument>> map, ConfigurationSection section, String path) {
        Map<String, List<String>> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            List<String> stringList = new ArrayList<>();
            value.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
            stringMap.put(key, stringList);
        });
        stringMap.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, List<MusicInstrument>> deserializeMusicInstrumentListMap(ConfigurationSection section, String path) {
        HashMap<String, List<MusicInstrument>> hashMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            List<MusicInstrument> list = new ArrayList<>();
            section.getStringList(path + "." + key).forEach(string -> {
                list.add(SerializationLib.deserializeMusicInstrument(string));
            });
            hashMap.put(key, list);
        });
        return hashMap;
    }

    public static void serializeMusicInstrumentList(List<MusicInstrument> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(MusicInstrument -> stringList.add(SerializationLib.serialize(MusicInstrument)));
        section.set(path, stringList);
    }

    public static List<MusicInstrument> deserializeMusicInstrumentList(ConfigurationSection section, String path) {
        List<MusicInstrument> MusicInstrumentList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                MusicInstrumentList.add(SerializationLib.deserializeMusicInstrument(string)));
        return MusicInstrumentList;
    }

    public static void serializeMusicInstrumentMap(Map<String, MusicInstrument> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, MusicInstrument> deserializeMusicInstrumentMap(ConfigurationSection section, String path) {
        HashMap<String, MusicInstrument> MusicInstrumentMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            MusicInstrumentMap.put(key, SerializationLib.deserializeMusicInstrument(string));
        });
        return MusicInstrumentMap;
    }
}
