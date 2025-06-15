package io.github.anjoismysign.bloblib.objects;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class SerializableSlotable {

    public static Slotable fromConfigurationSection(ConfigurationSection section) {
        ItemStack itemStack = SerializableItem.fromConfigurationSection(section.getConfigurationSection("ItemStack"));
        int slot = section.getInt("Slot", -1);
        if (slot < 0) {
            Bukkit.getLogger().info(section.getName() + "'s Slot is lower than '0'. This is probably due because there's no slot!!!");
            Bukkit.getLogger().info("In order to not break, we the plugin developers are defaulting it to '0' but it's highly probable that");
            Bukkit.getLogger().info("that you didn't intend this! Please check your configuration files!");
            slot = 0;
        }
        return new Slotable(slot, itemStack);
    }
}
