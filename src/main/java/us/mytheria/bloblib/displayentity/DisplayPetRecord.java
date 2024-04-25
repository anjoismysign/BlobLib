package us.mytheria.bloblib.displayentity;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
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
public record DisplayPetRecord(@Nullable ItemStack itemStack,
                               @Nullable BlockData blockData,
                               @Nullable Particle particle,
                               @Nullable String customName) {

    public static DisplayPetRecord read(ConfigurationSection section) {
        ItemStack itemStack = null;
        if (section.isConfigurationSection("ItemStack")) {
            itemStack = ItemStackReader.getInstance().readOrFailFast(section.getConfigurationSection("ItemStack"));
        }
        BlockData blockData = null;
        if (section.isString("BlockData")) {
            blockData = Bukkit.createBlockData(section.getString("BlockData"));
        }
        Particle particle = null;
        if (section.contains("Particle"))
            particle = SerializationLib.deserializeParticle(section.getString("Particle"));
        String customName = null;
        if (section.contains("CustomName"))
            customName = TextColor.PARSE(section.getString("CustomName"));
        if (itemStack == null && blockData == null) {
            BlobLib.getAnjoLogger().singleError("ItemStack and BlockData are both empty at " + section.getCurrentPath());
            return null;
        }
        return new DisplayPetRecord(itemStack, blockData, particle, customName);
    }

    public void serialize(ConfigurationSection configurationSection) {
        if (itemStack == null && blockData == null)
            throw new IllegalArgumentException("ItemStack and BlockData cannot both be null");
        if (itemStack != null)
            configurationSection.set("ItemStack", itemStack);
        if (blockData != null)
            configurationSection.set("BlockData", blockData.getAsString(true));
        if (particle != null)
            configurationSection.set("Particle", particle);
        if (customName != null)
            configurationSection.set("CustomName", customName);
    }
}
