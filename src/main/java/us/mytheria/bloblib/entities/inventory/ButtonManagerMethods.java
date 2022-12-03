package us.mytheria.bloblib.entities.inventory;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Set;

public interface ButtonManagerMethods {

    boolean contains(String key);

    boolean contains(int key);

    Set<Integer> get(String key);

    ItemStack get(int key);

    Collection<ItemStack> buttons();

    boolean add(ConfigurationSection section);

    boolean read(ConfigurationSection section);

    Collection<String> keys();
}
