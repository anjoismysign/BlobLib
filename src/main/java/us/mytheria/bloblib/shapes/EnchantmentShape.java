package us.mytheria.bloblib.shapes;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentShape {

    public static <U> HashMap<String, U> toStringKeys(Map<Enchantment, U> map) {
        HashMap<String, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.serialize(key), value));
        return newMap;
    }

    public static <U> HashMap<Enchantment, U> toEnchantmentKeys(Map<String, U> map) {
        HashMap<Enchantment, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeEnchantment(key), value));
        return newMap;
    }

    public static void serializeEnchantmentListMap(Map<String, List<Enchantment>> map, ConfigurationSection section, String path) {
        Map<String, List<String>> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            List<String> stringList = new ArrayList<>();
            value.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
            stringMap.put(key, stringList);
        });
        stringMap.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, List<Enchantment>> deserializeEnchantmentListMap(ConfigurationSection section, String path) {
        HashMap<String, List<Enchantment>> hashMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            List<Enchantment> list = new ArrayList<>();
            section.getStringList(path + "." + key).forEach(string -> {
                list.add(SerializationLib.deserializeEnchantment(string));
            });
            hashMap.put(key, list);
        });
        return hashMap;
    }

    public static void serializeEnchantmentList(List<Enchantment> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(Enchantment -> stringList.add(SerializationLib.serialize(Enchantment)));
        section.set(path, stringList);
    }

    public static List<Enchantment> deserializeEnchantmentList(ConfigurationSection section, String path) {
        List<Enchantment> EnchantmentList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                EnchantmentList.add(SerializationLib.deserializeEnchantment(string)));
        return EnchantmentList;
    }

    public static void serializeEnchantmentMap(Map<String, Enchantment> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, Enchantment> deserializeEnchantmentMap(ConfigurationSection section, String path) {
        HashMap<String, Enchantment> EnchantmentMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            EnchantmentMap.put(key, SerializationLib.deserializeEnchantment(string));
        });
        return EnchantmentMap;
    }
}
