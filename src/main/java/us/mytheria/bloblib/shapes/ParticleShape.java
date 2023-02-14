package us.mytheria.bloblib.shapes;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleShape {

    public static <U> HashMap<String, U> toStringKeys(Map<Particle, U> map) {
        HashMap<String, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.serialize(key), value));
        return newMap;
    }

    public static <U> HashMap<Particle, U> toParticleKeys(Map<String, U> map) {
        HashMap<Particle, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeParticle(key), value));
        return newMap;
    }

    public static void serializeParticleListMap(Map<String, List<Particle>> map, ConfigurationSection section, String path) {
        Map<String, List<String>> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            List<String> stringList = new ArrayList<>();
            value.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
            stringMap.put(key, stringList);
        });
        stringMap.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, List<Particle>> deserializeParticleListMap(ConfigurationSection section, String path) {
        HashMap<String, List<Particle>> hashMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            List<Particle> list = new ArrayList<>();
            section.getStringList(path + "." + key).forEach(string -> {
                list.add(SerializationLib.deserializeParticle(string));
            });
            hashMap.put(key, list);
        });
        return hashMap;
    }

    public static void serializeParticleList(List<Particle> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(Particle -> stringList.add(SerializationLib.serialize(Particle)));
        section.set(path, stringList);
    }

    public static List<Particle> deserializeParticleList(ConfigurationSection section, String path) {
        List<Particle> ParticleList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                ParticleList.add(SerializationLib.deserializeParticle(string)));
        return ParticleList;
    }

    public static void serializeParticleMap(Map<String, Particle> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, Particle> deserializeParticleMap(ConfigurationSection section, String path) {
        HashMap<String, Particle> ParticleMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            ParticleMap.put(key, SerializationLib.deserializeParticle(string));
        });
        return ParticleMap;
    }
}
