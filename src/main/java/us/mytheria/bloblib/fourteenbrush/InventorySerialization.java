package us.mytheria.bloblib.fourteenbrush;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class InventorySerialization {
    public static String itemStackArrayToBase64(ItemStack[] itemStacks) throws IllegalStateException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            // write the size of the inventory
            dataOutput.writeInt(itemStacks.length);
            // save every element
            for (ItemStack itemStack : itemStacks) {
                dataOutput.writeObject(itemStack);
            }
            // serialize that array
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
            // return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save items stacks.", e);
        }
    }

    public static String catchItemStackArrayToBase64(ItemStack[] itemStacks) {
        try {
            return itemStackArrayToBase64(itemStacks);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String inventoryToBase64(Inventory inventory) throws IllegalStateException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());
            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }
            // Serialize that array
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
            // return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save items stacks.", e);
        }
    }

    public static String catchInventoryToBase64(Inventory inventory) {
        try {
            return inventoryToBase64(inventory);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Inventory inventoryFromBase64(String data) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());
            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    public static Inventory catchInventoryFromBase64(String data) {
        try {
            return inventoryFromBase64(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack[] itemStackArrayFromBase64(String data) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            ItemStack[] items = new ItemStack[dataInput.readInt()];
            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            return items;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ItemStack[0];
    }
}