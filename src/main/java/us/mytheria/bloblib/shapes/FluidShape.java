package us.mytheria.bloblib.shapes;

import org.bukkit.Fluid;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluidShape {

    public static <U> HashMap<String, U> fluidToStringKeys(Map<Fluid, U> map) {
        HashMap<String, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.serialize(key), value));
        return newMap;
    }

    public static void serializeFluidListMap(Map<String, List<Fluid>> map, ConfigurationSection section, String path) {
        Map<String, List<String>> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            List<String> stringList = new ArrayList<>();
            value.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
            stringMap.put(key, stringList);
        });
        stringMap.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, List<Fluid>> deserializeFluidListMap(ConfigurationSection section, String path) {
        HashMap<String, List<Fluid>> hashMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            List<Fluid> list = new ArrayList<>();
            section.getStringList(path + "." + key).forEach(string -> {
                list.add(SerializationLib.deserializeFluid(string));
            });
            hashMap.put(key, list);
        });
        return hashMap;
    }

    public static void serializeFluidList(List<Fluid> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(Fluid -> stringList.add(SerializationLib.serialize(Fluid)));
        section.set(path, stringList);
    }

    public static List<Fluid> deserializeFluidList(ConfigurationSection section, String path) {
        List<Fluid> FluidList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                FluidList.add(SerializationLib.deserializeFluid(string)));
        return FluidList;
    }

    public static void serializeFluidMap(Map<String, Fluid> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, Fluid> deserializeFluidMap(ConfigurationSection section, String path) {
        HashMap<String, Fluid> FluidMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            FluidMap.put(key, SerializationLib.deserializeFluid(string));
        });
        return FluidMap;
    }
}
