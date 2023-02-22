package us.mytheria.bloblib.floatingpet;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import us.mytheria.bloblib.itemstack.ItemStackBuilder;
import us.mytheria.bloblib.itemstack.ItemStackReader;
import us.mytheria.bloblib.utilities.SerializationLib;

public class FloatingPetReader {

    public static FloatingPetData read(ConfigurationSection section) {
        ItemStackBuilder builder = ItemStackReader.read(section.getConfigurationSection("ItemStack"));
        ItemStack itemStack = builder.build();
        Particle particle = null;
        if (section.contains("Particle"))
            particle = SerializationLib.deserializeParticle(section.getString("Particle"));
        String customName = null;
        if (section.contains("CustomName"))
            customName = section.getString("CustomName");

        return new FloatingPetData(itemStack, particle, customName);
    }
}
