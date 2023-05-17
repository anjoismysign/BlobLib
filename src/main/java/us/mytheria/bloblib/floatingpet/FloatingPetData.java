package us.mytheria.bloblib.floatingpet;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FloatingPetData {
    private final ItemStack itemStack;
    @Nullable
    private final Particle particle;
    @Nullable
    private final String customName;

    /**
     * Will hold all attributes for a FloatingPet
     * except the owner themselves. The idea behind
     * it is that FloatingPetData could be stored
     * in a manager and later used to make new instances
     * of FloatingPets.
     *
     * @param record the FloatingPetRecord to pass from
     */
    public FloatingPetData(FloatingPetRecord record) {
        this.itemStack = record.itemStack();
        this.particle = record.particle();
        this.customName = record.customName();
    }

    public ArmorStandFloatingPet toPlayer(Player owner) {
        return new ArmorStandFloatingPet(owner, itemStack, particle, customName);
    }

    public ArmorStandFloatingPet toPlayerAndParsePlaceholder(Player owner, String ownerPlaceholder) {
        ArmorStandFloatingPet floatingPet = toPlayer(owner);
        floatingPet.rename(floatingPet.getCustomName().replace(ownerPlaceholder, owner.getName()));
        return floatingPet;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Nullable
    public Particle getParticle() {
        return particle;
    }

    @Nullable
    public String getCustomName() {
        return customName;
    }

    public void serialize(ConfigurationSection configurationSection, String path) {
        FloatingPetRecord record = new FloatingPetRecord(itemStack, particle, customName);
        record.serialize(configurationSection, path);
    }
}
