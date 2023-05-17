package us.mytheria.bloblib.displayentity;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DisplayPetData {
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
     * From now on, you will be required to pass
     * a FloatingPetRecord to the FloatingPetData
     * in all minor classes.
     *
     * @param record the FloatingPetRecord to pass from
     */
    public DisplayPetData(DisplayPetRecord record) {
        this.itemStack = record.itemStack();
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
        return new ArmorStandFloatingPet(owner, itemStack, particle, customName);
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
     * Will get the ItemStack of the FloatingPetData
     *
     * @return the ItemStack of the FloatingPetData
     */
    public ItemStack getItemStack() {
        return itemStack;
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
     * under the patch name.
     * Example:
     * ConfigurationSection section = YamlConfiguration.loadConfiguration(file);
     * FloatingPetData data = new FloatingPetData(new ItemStack(Material.DIAMOND),
     * Particle.FLAME, "%owner%'s Pet");
     * data.serialize(section, "FloatingPetRecord");
     * // YAML file would look like:
     * // FloatingPetRecord:
     * //   ItemStack:
     * //     #blablabla
     * //   Particle: FLAME
     * //   CustomName: "%owner%'s Pet"
     *
     * @param configurationSection the configuration section to serialize in
     * @param path                 the path to serialize in
     */
    public void serialize(ConfigurationSection configurationSection, String path) {
        DisplayPetRecord record = new DisplayPetRecord(itemStack, particle, customName);
        record.serialize(configurationSection, path);
    }
}
