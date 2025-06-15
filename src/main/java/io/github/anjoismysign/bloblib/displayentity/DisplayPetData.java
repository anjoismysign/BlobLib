package io.github.anjoismysign.bloblib.displayentity;

import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DisplayPetData {
    @Nullable
    private final ItemStack itemStack;
    @Nullable
    private final BlockData blockData;
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
     * From now on, you will be required to pass
     * a FloatingPetRecord to the FloatingPetData
     * in all minor classes.
     *
     * @param record the FloatingPetRecord to pass from
     */
    public DisplayPetData(DisplayPetRecord record) {
        this.itemStack = record.itemStack();
        this.blockData = record.blockData();
        this.particle = record.particle();
        this.customName = record.customName();
    }

    /**
     * Will create a new instance of ArmorStandFloatingPet
     *
     * @param owner the owner of the pet
     * @return a new instance of ArmorStandFloatingPet
     */
    public ArmorStandFloatingPet asArmorStand(Player owner) {
        if (itemStack == null || itemStack.getType().isAir())
            throw new IllegalStateException("ItemStack cannot be null nor be air");
        return new ArmorStandFloatingPet(owner, itemStack, particle, customName,
                EntityAnimationsCarrier.DEFAULT());
    }

    /**
     * Will create a new instance of ItemDisplayFloatingPet
     *
     * @param owner the owner of the pet
     * @return a new instance of ItemDisplayFloatingPet
     */
    public ItemDisplayFloatingPet asItemDisplay(Player owner) {
        if (itemStack == null || itemStack.getType().isAir())
            throw new IllegalStateException("ItemStack cannot be null nor be air");
        return new ItemDisplayFloatingPet(owner, itemStack, particle, customName,
                DisplayFloatingPetSettings.DEFAULT());
    }

    /**
     * Will create a new instance of ArmorStandFloatingPet
     * and parse the placeholder with the owner's name.
     * Example:
     * FloatingPetData test = new FloatingPetData(new ItemStack(Material.DIAMOND),
     * Particle.FLAME, "%owner%'s Pet");
     * Player player = Bukkit.getPlayer("Notch");
     * ArmorStandFloatingPet floatingPet = test.asArmorStandAndParsePlaceHolder(player, "%owner%");
     * assert floatingPet.getCustomName().equals("Notch's Pet");
     * // true
     *
     * @param owner            the owner of the pet
     * @param ownerPlaceholder the placeholder to replace with the owner's name
     * @return a new instance of ArmorStandFloatingPet
     */
    public ArmorStandFloatingPet asArmorStandAndParsePlaceHolder(Player owner, String ownerPlaceholder) {
        ArmorStandFloatingPet floatingPet = asArmorStand(owner);
        floatingPet.setCustomName(floatingPet.getCustomName().replace(ownerPlaceholder, owner.getName()));
        return floatingPet;
    }

    /**
     * Will create a new instance of ItemDisplayFloatingPet
     * and parse the placeholder with the owner's name.
     * Example:
     * FloatingPetData test = new FloatingPetData(new ItemStack(Material.DIAMOND),
     * Particle.FLAME, "%owner%'s Pet");
     * Player player = Bukkit.getPlayer("Notch");
     * ItemDisplayFloatingPet floatingPet = test.asItemDisplayAndParsePlaceHolder(player, "%owner%");
     * assert floatingPet.getCustomName().equals("Notch's Pet");
     * // true
     *
     * @param owner            the owner of the pet
     * @param ownerPlaceholder the placeholder to replace with the owner's name
     * @return a new instance of ItemDisplayFloatingPet
     */
    public ItemDisplayFloatingPet asItemDisplayAndParsePlaceHolder(Player owner, String ownerPlaceholder) {
        ItemDisplayFloatingPet floatingPet = asItemDisplay(owner);
        floatingPet.setCustomName(floatingPet.getCustomName().replace(ownerPlaceholder, owner.getName()));
        return floatingPet;
    }

    /**
     * Will get the ItemStack of the FloatingPetData
     * if available. Null otherwise.
     *
     * @return the ItemStack of the FloatingPetData,
     * null otherwise
     */
    @Nullable
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Will get the BlockData of the FloatingPetData
     * if available. Null otherwise.
     *
     * @return the BlockData of the FloatingPetData,
     * null otherwise
     */
    @Nullable
    public BlockData getBlockData() {
        return blockData;
    }

    /**
     * Will get the Particle of the FloatingPetData
     *
     * @return the Particle of the FloatingPetData
     */
    @Nullable
    public Particle getParticle() {
        return particle;
    }

    /**
     * Will get the custom name of the FloatingPetData
     *
     * @return the custom name of the FloatingPetData
     */
    @Nullable
    public String getCustomName() {
        return customName;
    }

    /**
     * Will serialize the FloatingPetData in a configuration section
     * Example:
     * ConfigurationSection section = YamlConfiguration.loadConfiguration(file);
     * FloatingPetData data = new FloatingPetData(new ItemStack(Material.DIAMOND),
     * Particle.FLAME, "%owner%'s Pet");
     * data.serialize(section);
     * // YAML file would look like:
     * // ItemStack:
     * //   #blablabla
     * // Particle: FLAME
     * // CustomName: "%owner%'s Pet"
     *
     * @param configurationSection the configuration section to serialize in
     */
    public void serialize(ConfigurationSection configurationSection) {
        DisplayPetRecord record = new DisplayPetRecord(itemStack, blockData, particle, customName);
        record.serialize(configurationSection);
    }
}
