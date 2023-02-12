package us.mytheria.bloblib.utilities;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MapLib {

    public static <U> HashMap<Byte, U> toByteKeys(Map<String, U> map) {
        HashMap<Byte, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(Byte.parseByte(key), value));
        return newMap;
    }

    public static <U> HashMap<Short, U> toShortKeys(Map<String, U> map) {
        HashMap<Short, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(Short.parseShort(key), value));
        return newMap;
    }

    public static <U> HashMap<Integer, U> toIntegerKeys(Map<String, U> map) {
        HashMap<Integer, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(Integer.parseInt(key), value));
        return newMap;
    }

    public static <U> HashMap<Long, U> toLongKeys(Map<String, U> map) {
        HashMap<Long, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(Long.parseLong(key), value));
        return newMap;
    }

    public static <U> HashMap<Float, U> toFloatKeys(Map<String, U> map) {
        HashMap<Float, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(Float.parseFloat(key), value));
        return newMap;
    }

    public static <U> HashMap<Double, U> toDoubleKeys(Map<String, U> map) {
        HashMap<Double, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(Double.parseDouble(key), value));
        return newMap;
    }

    public static <U> HashMap<Boolean, U> toBooleanKeys(Map<String, U> map) {
        HashMap<Boolean, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(Boolean.parseBoolean(key), value));
        return newMap;
    }

    public static <U> HashMap<Character, U> toCharacterKeys(Map<String, U> map) {
        HashMap<Character, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(key.charAt(0), value));
        return newMap;
    }

    public static <U> HashMap<BigInteger, U> toBigIntegerKeys(Map<String, U> map) {
        HashMap<BigInteger, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeBigInteger(key), value));
        return newMap;
    }

    public static <U> HashMap<BigDecimal, U> toBigDecimalKeys(Map<String, U> map) {
        HashMap<BigDecimal, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeBigDecimal(key), value));
        return newMap;
    }

    public static <U> HashMap<Vector, U> toVectorKeys(Map<String, U> map) {
        HashMap<Vector, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeVector(key), value));
        return newMap;
    }

    public static <U> HashMap<BlockVector, U> toBlockVectorKeys(Map<String, U> map) {
        HashMap<BlockVector, U> newMap = new HashMap<>();
        map.forEach((key, value) ->
                newMap.put(SerializationLib.deserializeBlockVector(key), value));
        return newMap;
    }

    public static <U> HashMap<Location, U> toLocationKeys(Map<String, U> map) {
        HashMap<Location, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeLocation(key), value));
        return newMap;
    }

    public static <U> HashMap<Block, U> toBlockKeys(Map<String, U> map) {
        HashMap<Block, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeBlock(key), value));
        return newMap;
    }

    public static <U> HashMap<Color, U> toColorKeys(Map<String, U> map) {
        HashMap<Color, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeColor(key), value));
        return newMap;
    }

    public static <U> HashMap<OfflinePlayer, U> toOfflinePlayerKeys(Map<String, U> map) {
        HashMap<OfflinePlayer, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeOfflinePlayer(key), value));
        return newMap;
    }

    public static <U> HashMap<UUID, U> toUUIDKeys(Map<String, U> map) {
        HashMap<UUID, U> newMap = new HashMap<>();
        map.forEach((key, value) -> newMap.put(SerializationLib.deserializeUUID(key), value));
        return newMap;
    }
}
