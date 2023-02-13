package us.mytheria.bloblib.shapes;

import org.bukkit.Instrument;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstrumentShape {

    public static <U> HashMap<String, U> instrumentToStringKeys(Map<Instrument, U> map) {
        HashMap<String, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.serialize(key), value));
        return newMap;
    }

    public static void serializeInstrumentListMap(Map<String, List<Instrument>> map, ConfigurationSection section, String path) {
        Map<String, List<String>> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            List<String> stringList = new ArrayList<>();
            value.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
            stringMap.put(key, stringList);
        });
        stringMap.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, List<Instrument>> deserializeInstrumentListMap(ConfigurationSection section, String path) {
        HashMap<String, List<Instrument>> hashMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            List<Instrument> list = new ArrayList<>();
            section.getStringList(path + "." + key).forEach(string -> {
                list.add(SerializationLib.deserializeInstrument(string));
            });
            hashMap.put(key, list);
        });
        return hashMap;
    }

    public static void serializeInstrumentList(List<Instrument> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(Instrument -> stringList.add(SerializationLib.serialize(Instrument)));
        section.set(path, stringList);
    }

    public static List<Instrument> deserializeInstrumentList(ConfigurationSection section, String path) {
        List<Instrument> InstrumentList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                InstrumentList.add(SerializationLib.deserializeInstrument(string)));
        return InstrumentList;
    }

    public static void serializeInstrumentMap(Map<String, Instrument> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, Instrument> deserializeInstrumentMap(ConfigurationSection section, String path) {
        HashMap<String, Instrument> InstrumentMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            InstrumentMap.put(key, SerializationLib.deserializeInstrument(string));
        });
        return InstrumentMap;
    }
}
