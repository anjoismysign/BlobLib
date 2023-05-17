package us.mytheria.bloblib.displayentity;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import us.mytheria.bloblib.itemstack.ItemStackBuilder;
import us.mytheria.bloblib.itemstack.ItemStackReader;
import us.mytheria.bloblib.utilities.SerializationLib;
import us.mytheria.bloblib.utilities.TextColor;

/**
 * A data carrier for the basic FloatingPet data.
 * The idea for a more complex data carrier is that
 * you extend FloatingPetData and implement your
 * own FloatingPetReader which will inject into
 * the FloatingPetData.
 *
 * @param itemStack  the ItemStack to display
 * @param particle   the particle to display
 * @param customName the custom name to display
 */
public record DisplayPetRecord(ItemStack itemStack, Particle particle, String customName) {
    public static DisplayPetRecord read(ConfigurationSection section) {
        ItemStackBuilder builder = ItemStackReader.read(section.getConfigurationSection("ItemStack"));
        ItemStack itemStack = builder.build();
        Particle particle = null;
        if (section.contains("Particle"))
            particle = SerializationLib.deserializeParticle(section.getString("Particle"));
        String customName = null;
        if (section.contains("CustomName"))
            customName = TextColor.PARSE(section.getString("CustomName"));

        return new DisplayPetRecord(itemStack, particle, customName);
    }

    public void serialize(ConfigurationSection configurationSection, String path) {
        configurationSection.set(path + ".ItemStack", itemStack);
        if (particle != null)
            configurationSection.set(path + ".Particle", particle);
        if (customName != null)
            configurationSection.set(path + ".CustomName", customName);
    }
}
