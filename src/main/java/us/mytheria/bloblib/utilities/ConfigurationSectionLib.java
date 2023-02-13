package us.mytheria.bloblib.utilities;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class ConfigurationSectionLib {

    public static void serializeStringMap(Map<String, String> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, String> deserializeStringMap(ConfigurationSection section, String path) {
        HashMap<String, String> stringMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            stringMap.put(key, section.getString(path + "." + key));
        });
        return stringMap;
    }

    public static void serializeByteMap(Map<String, Byte> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, Byte> deserializeByteMap(ConfigurationSection section, String path) {
        HashMap<String, Byte> byteMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            byteMap.put(key, (byte) section.getInt(path + "." + key));
        });
        return byteMap;
    }

    public static void serializeShortMap(Map<String, Short> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, Short> deserializeShortMap(ConfigurationSection section, String path) {
        HashMap<String, Short> shortMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            shortMap.put(key, (short) section.getInt(path + "." + key));
        });
        return shortMap;
    }

    public static void serializeIntegerMap(Map<String, Integer> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, Integer> deserializeIntegerMap(ConfigurationSection section, String path) {
        HashMap<String, Integer> integerMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            integerMap.put(key, section.getInt(path + "." + key));
        });
        return integerMap;
    }

    public static void serializeLongMap(Map<String, Long> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, Long> deserializeLongMap(ConfigurationSection section, String path) {
        HashMap<String, Long> longMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            longMap.put(key, section.getLong(path + "." + key));
        });
        return longMap;
    }

    public static void serializeFloatMap(Map<String, Float> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, Float> deserializeFloatMap(ConfigurationSection section, String path) {
        HashMap<String, Float> floatMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            floatMap.put(key, (float) section.getDouble(path + "." + key));
        });
        return floatMap;
    }

    public static void serializeDoubleMap(Map<String, Double> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, Double> deserializeDoubleMap(ConfigurationSection section, String path) {
        HashMap<String, Double> doubleMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            doubleMap.put(key, section.getDouble(path + "." + key));
        });
        return doubleMap;
    }

    public static void serializeCharacterMap(Map<String, Character> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, Character> deserializeCharacterMap(ConfigurationSection section, String path) {
        HashMap<String, Character> characterMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            characterMap.put(key, section.getString(path + "." + key).charAt(0));
        });
        return characterMap;
    }

    public static void serializeBooleanMap(Map<String, Boolean> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, value));
    }

    public static HashMap<String, Boolean> deserializeBooleanMap(ConfigurationSection section, String path) {
        HashMap<String, Boolean> booleanMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            booleanMap.put(key, section.getBoolean(path + "." + key));
        });
        return booleanMap;
    }

    public static void serializeVectorList(List<Vector> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
        section.set(path, stringList);
    }

    public static List<Vector> deserializeVectorList(ConfigurationSection section, String path) {
        List<BlockVector> test = new ArrayList<>();
        List<Vector> vectorList = new ArrayList<>();
        section.getStringList(path).forEach(string -> {
            String[] split = string.split(", ");
            vectorList.add(SerializationLib.deserializeVector(string));
        });
        return vectorList;
    }

    public static void serializeVectorMap(Map<String, Vector> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, Vector> deserializeVectorMap(ConfigurationSection section, String path) {
        HashMap<String, Vector> vectorMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            vectorMap.put(key, SerializationLib.deserializeVector(string));
        });
        return vectorMap;
    }

    public static void serializeBlockVectorList(List<BlockVector> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(vector -> stringList.add(SerializationLib.serialize(vector)));
        section.set(path, stringList);
    }

    public static List<BlockVector> deserializeBlockVectorList(ConfigurationSection section, String path) {
        List<BlockVector> vectorList = new ArrayList<>();
        section.getStringList(path).forEach(string -> {
            String[] split = string.split(", ");
            vectorList.add(SerializationLib.deserializeBlockVector(string));
        });
        return vectorList;
    }

    public static void serializeBlockVectorMap(Map<String, BlockVector> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, BlockVector> deserializeBlockVectorMap(ConfigurationSection section, String path) {
        HashMap<String, BlockVector> vectorMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            vectorMap.put(key, SerializationLib.deserializeBlockVector(string));
        });
        return vectorMap;
    }

    public static void serializeLocationList(List<Location> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(location -> stringList.add(SerializationLib.serialize(location)));
        section.set(path, stringList);
    }

    public static List<Location> deserializeLocationList(ConfigurationSection section, String path) {
        List<Location> locationList = new ArrayList<>();
        section.getStringList(path).forEach(string -> {
            String[] split = string.split(", ");
            locationList.add(SerializationLib.deserializeLocation(string));
        });
        return locationList;
    }

    public static void serializeLocationMap(Map<String, Location> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, Location> deserializeLocationMap(ConfigurationSection section, String path) {
        HashMap<String, Location> locationMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            locationMap.put(key, SerializationLib.deserializeLocation(string));
        });
        return locationMap;
    }

    public static void serializeBlockList(List<Block> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(block -> stringList.add(SerializationLib.serialize(block)));
        section.set(path, stringList);
    }

    public static List<Block> deserializeBlockList(ConfigurationSection section, String path) {
        List<Block> blockList = new ArrayList<>();
        section.getStringList(path).forEach(string -> {
            String[] split = string.split(", ");
            blockList.add(SerializationLib.deserializeBlock(string));
        });
        return blockList;
    }

    public static void serializeBlockMap(Map<String, Block> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, Block> deserializeBlockMap(ConfigurationSection section, String path) {
        HashMap<String, Block> blockMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            blockMap.put(key, SerializationLib.deserializeBlock(string));
        });
        return blockMap;
    }

    public static void serializeColorList(List<Color> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(color -> stringList.add(SerializationLib.serialize(color)));
        section.set(path, stringList);
    }

    public static List<Color> deserializeColorList(ConfigurationSection section, String path) {
        List<Color> colorList = new ArrayList<>();
        section.getStringList(path).forEach(string -> {
            String[] split = string.split(", ");
            colorList.add(SerializationLib.deserializeColor(string));
        });
        return colorList;
    }

    public static void serializeColorMap(Map<String, Color> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, Color> deserializeColorMap(ConfigurationSection section, String path) {
        HashMap<String, Color> colorMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            colorMap.put(key, SerializationLib.deserializeColor(string));
        });
        return colorMap;
    }

    public static void serializeOfflinePlayer(List<OfflinePlayer> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(player -> stringList.add(SerializationLib.serialize(player)));
        section.set(path, stringList);
    }

    public static List<OfflinePlayer> deserializeOfflinePlayerList(ConfigurationSection section, String path) {
        List<OfflinePlayer> offlinePlayerList = new ArrayList<>();
        section.getStringList(path).forEach(string -> offlinePlayerList
                .add(SerializationLib.deserializeOfflinePlayer(string)));
        return offlinePlayerList;
    }

    public static void serializeOfflinePlayerMap(Map<String, OfflinePlayer> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, OfflinePlayer> deserializeOfflinePlayerMap(ConfigurationSection section, String path) {
        HashMap<String, OfflinePlayer> offlinePlayerMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            offlinePlayerMap.put(key, SerializationLib.deserializeOfflinePlayer(string));
        });
        return offlinePlayerMap;
    }

    public static void serializeUUIDList(List<UUID> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(uuid -> stringList.add(SerializationLib.serialize(uuid)));
        section.set(path, stringList);
    }

    public static List<UUID> deserializeUUIDList(ConfigurationSection section, String path) {
        List<UUID> uuidList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                uuidList.add(SerializationLib.deserializeUUID(string)));
        return uuidList;
    }

    public static void serializeUUIDMap(Map<String, UUID> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, UUID> deserializeUUIDMap(ConfigurationSection section, String path) {
        HashMap<String, UUID> uuidMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            uuidMap.put(key, SerializationLib.deserializeUUID(string));
        });
        return uuidMap;
    }

    public static void serializeBigIntegerList(List<BigInteger> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(bigInteger -> stringList.add(SerializationLib.serialize(bigInteger)));
        section.set(path, stringList);
    }

    public static List<BigInteger> deserializeBigIntegerList(ConfigurationSection section, String path) {
        List<BigInteger> bigIntegerList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                bigIntegerList.add(SerializationLib.deserializeBigInteger(string)));
        return bigIntegerList;
    }

    public static void serializeBigIntegerMap(Map<String, BigInteger> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, BigInteger> deserializeBigIntegerMap(ConfigurationSection section, String path) {
        HashMap<String, BigInteger> bigIntegerMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            bigIntegerMap.put(key, SerializationLib.deserializeBigInteger(string));
        });
        return bigIntegerMap;
    }

    public static void serializeBigDecimalList(List<BigDecimal> list, ConfigurationSection section, String path) {
        List<String> stringList = new ArrayList<>();
        list.forEach(bigDecimal -> stringList.add(SerializationLib.serialize(bigDecimal)));
        section.set(path, stringList);
    }

    public static List<BigDecimal> deserializeBigDecimalList(ConfigurationSection section, String path) {
        List<BigDecimal> bigDecimalList = new ArrayList<>();
        section.getStringList(path).forEach(string ->
                bigDecimalList.add(SerializationLib.deserializeBigDecimal(string)));
        return bigDecimalList;
    }

    public static void serializeBigDecimalMap(Map<String, BigDecimal> map, ConfigurationSection section, String path) {
        map.forEach((key, value) -> section.set(path + "." + key, SerializationLib.serialize(value)));
    }

    public static HashMap<String, BigDecimal> deserializeBigDecimalMap(ConfigurationSection section, String path) {
        HashMap<String, BigDecimal> bigDecimalMap = new HashMap<>();
        section.getConfigurationSection(path).getKeys(false).forEach(key -> {
            String string = section.getString(path + "." + key);
            bigDecimalMap.put(key, SerializationLib.deserializeBigDecimal(string));
        });
        return bigDecimalMap;
    }
}
