package us.mytheria.bloblib.floatingpet;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import us.mytheria.bloblib.itemstack.ItemStackBuilder;
import us.mytheria.bloblib.itemstack.ItemStackReader;
import us.mytheria.bloblib.utilities.SerializationLib;

/**
 * A data carrier for the basic FloatingPet data.
 * The idea for a more complex data carrier is that
 * you extend FloatingPetData and implement your
 * own FloatingPetReader which will inject into
 * the FloatingPetData. We recommend to not allow
 * any type of FloatingPetReader as a parameter
 * for any FloatingPet constructor.
 *
 * @param itemStack  the ItemStack to display
 * @param particle   the particle to display
 * @param customName the custom name to display
 */
public record FloatingPetReader(ItemStack itemStack, Particle particle, String customName) {
    public static FloatingPetReader read(ConfigurationSection section) {
        ItemStackBuilder builder = ItemStackReader.read(section.getConfigurationSection("ItemStack"));
        ItemStack itemStack = builder.build();
        Particle particle = null;
        if (section.contains("Particle"))
            particle = SerializationLib.deserializeParticle(section.getString("Particle"));
        String customName = null;
        if (section.contains("CustomName"))
            customName = section.getString("CustomName");

        return new FloatingPetReader(itemStack, particle, customName);
    }
}
