package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class ObjectBuilder extends BlobInventory {
    private final UUID builderId;

    public ObjectBuilder(BlobInventory blobInventory, UUID builderId) {
        super(blobInventory.getTitle(), blobInventory.getSize(), blobInventory.getButtonManager());
        this.builderId = builderId;
    }

    /**
     * @return player matching builderId.
     * null if no player is found.
     */
    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(builderId);
    }

    public UUID getBuilderId() {
        return builderId;
    }

    public void openInventory() {
        getPlayer().openInventory(getInventory());
    }

    public void updateDefaultButton(String key, String oldString, String newString) {
        this.getSlots(key).forEach(i -> {
            ItemStack itemStack = cloneDefaultButton(key);
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> newLore = new ArrayList<>();
            itemMeta.getLore().forEach(s -> newLore.add(s.replace(oldString, newString)));
            itemMeta.setLore(newLore);
            itemStack.setItemMeta(itemMeta);
            this.setButton(i, itemStack);
        });
    }

    public boolean isBuildButton(int slot) {
        return getSlots("Build").contains(slot);
    }
}
