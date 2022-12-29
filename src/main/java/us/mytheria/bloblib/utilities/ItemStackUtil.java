package us.mytheria.bloblib.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class ItemStackUtil {
    public static String display(ItemStack itemStack) {
        if (itemStack == null)
            return "null";
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName())
            return itemStack.getItemMeta().getDisplayName();
        return itemStack.getType().name();
    }

    @Nullable
    public static String serialize(ItemStack itemStack) {
        if (itemStack == null)
            return "null";
        if (!itemStack.hasItemMeta()) {
            Bukkit.getLogger().info("No ItemMeta found for " + itemStack.getType());
            return "null";
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        String customModelData = (itemMeta.hasCustomModelData() ? String.valueOf(itemMeta.getCustomModelData()) : "0");
        String toReturn = itemMeta.getDisplayName() + "%bis:%" + StringUtil.listStringCompactor(itemMeta.getLore()) + "%bis:%" + customModelData + "%bis:%" + itemStack.getType();
        return toReturn;
    }

    public static ItemStack deserialize(String serialized) {
        if (serialized.equals("null"))
            return null;
        String[] split = serialized.split("%bis:%");
        ItemStack itemStack = new ItemStack(Material.valueOf(split[3]));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(split[0]);
        itemMeta.setLore(StringUtil.listStringDecompactor(split[1]));
        itemMeta.setCustomModelData(Integer.parseInt(split[2]));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Nullable
    public static String itemStackArrayToBase64(ItemStack[] itemStacks) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeInt(itemStacks.length);
            for (ItemStack itemStack : itemStacks) {
                objectOutputStream.writeObject(itemStack);
            }
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static ItemStack[] itemStackArrayFromBase64(String serialized) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(serialized));
             BukkitObjectInputStream objectInputStream = new BukkitObjectInputStream(byteArrayInputStream)) {
            ItemStack[] itemStacks = new ItemStack[objectInputStream.readInt()];
            for (int i = 0; i < itemStacks.length; i++) {
                itemStacks[i] = (ItemStack) objectInputStream.readObject();
            }
            return itemStacks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
