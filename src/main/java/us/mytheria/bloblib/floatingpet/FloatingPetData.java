package us.mytheria.bloblib.floatingpet;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record FloatingPetData(ItemStack itemStack, Particle particle, String customName) {
    public FloatingPet toPlayer(Player owner) {
        return new FloatingPet(owner, itemStack, particle, customName);
    }

    public FloatingPet toPlayerAndParsePlaceholder(Player owner, String ownerPlaceholder) {
        FloatingPet floatingPet = toPlayer(owner);
        floatingPet.setCustomName(floatingPet.getCustomName().replace(ownerPlaceholder, owner.getName()));
        return floatingPet;
    }

}
