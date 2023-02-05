package us.mytheria.bloblib.utilities;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class ItemStackSerializer {

    /**
     * Serializes an ItemStack to a Base64 String
     *
     * @param itemStack The ItemStack to serialize
     * @return The Base64 String, null if the ItemStack is null or if found an exception
     */
    public static String toBase64(ItemStack itemStack) {
        if (itemStack == null)
            return null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(itemStack);
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Deserializes an ItemStack from a Base64 String
     *
     * @param base64 The Base64 String to deserialize
     * @return The ItemStack, null if the String is null or if found an exception
     */
    public static ItemStack fromBase64(String base64) {
        if (base64 == null) {
            return null;
        }
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
             BukkitObjectInputStream objectInputStream = new BukkitObjectInputStream(byteArrayInputStream)) {
            ItemStack itemStack = (ItemStack) objectInputStream.readObject();
            return itemStack;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean serialize(ItemStack itemStack, YamlConfiguration configuration) {
        return serialize(itemStack, configuration, "ItemStack");
    }

    public static boolean serialize(ItemStack itemStack, YamlConfiguration configuration, String path) {
        String base64 = toBase64(itemStack);
        if (base64 == null)
            return false;
        configuration.set(path, base64);
        return true;
    }

    /**
     * Deserializes an ItemStack from a YamlConfiguration
     *
     * @param configuration The YamlConfiguration to deserialize
     * @param path          The path to the ItemStack
     * @return The ItemStack, null if the String is null or if found an exception
     */
    public static ItemStack deserialize(YamlConfiguration configuration, String path) {
        return fromBase64(configuration.getString(path));
    }

    /**
     * Deserializes an ItemStack from a YamlConfiguration from the default path "ItemStack"
     *
     * @param configuration The YamlConfiguration to deserialize
     * @return The ItemStack, null if the String is null or if found an exception
     */
    public static ItemStack deserialize(YamlConfiguration configuration) {
        return deserialize(configuration, "ItemStack");
    }

}
