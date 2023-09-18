package us.mytheria.bloblib.entities.currency;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.managers.ManagerDirector;

//Under development
public class TangibleCurrencyListener implements Listener {
    private final ManagerDirector director;

    public TangibleCurrencyListener(ManagerDirector director) {
        this.director = director;
    }

    @Nullable
    private String isCurrency(ItemStack itemStack) {
        if (itemStack == null)
            return null;
        if (itemStack.getType() == Material.AIR)
            return null;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return null;
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        if (!pdc.has(director.getNamespacedKey("tangibleCurrencyKey"),
                PersistentDataType.STRING))
            return null;
        return pdc.get(director.getNamespacedKey("tangibleCurrencyKey"),
                PersistentDataType.STRING);
    }
}
