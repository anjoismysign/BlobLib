package us.mytheria.bloblib.floatingpet;

import org.bukkit.Particle;
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
     * We recommend not to allow any type of FloatingPetReader
     * as a parameter for any FloatingPet constructor but their
     * attributes instead as it will take more time for maintaining
     * your code in case of later wanting to extend your custom
     * FloatingPet object!
     * Don't make FloatingPetData a record unless you are sure
     * you won't need to extend it in the future!
     *
     * @param itemStack  the ItemStack to display
     * @param particle   the particle to display
     * @param customName the custom name to display
     */
    public FloatingPetData(ItemStack itemStack, @Nullable Particle particle, @Nullable String customName) {
        this.itemStack = itemStack;
        this.particle = particle;
        this.customName = customName;
    }

    public FloatingPet toPlayer(Player owner) {
        return new FloatingPet(owner, itemStack, particle, customName);
    }

    public FloatingPet toPlayerAndParsePlaceholder(Player owner, String ownerPlaceholder) {
        FloatingPet floatingPet = toPlayer(owner);
        floatingPet.setCustomName(floatingPet.getCustomName().replace(ownerPlaceholder, owner.getName()));
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
}
