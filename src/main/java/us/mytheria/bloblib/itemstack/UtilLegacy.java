package us.mytheria.bloblib.itemstack;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mytheria.bloblib.utilities.ItemStackUtilMiddleman;

import java.util.ArrayList;
import java.util.List;

public class UtilLegacy implements ItemStackUtilMiddleman {
    private static UtilLegacy instance;

    public static UtilLegacy getInstance() {
        if (instance == null) {
            instance = new UtilLegacy();
        }
        return instance;
    }

    public String display(ItemStack itemStack) {
        if (itemStack == null)
            return "null";
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName())
            return itemStack.getItemMeta().getDisplayName();
        return itemStack.getType().name();
    }

    public void replace(ItemStack itemStack, String target, String replacement) {
        if (itemStack == null)
            return;
        if (!itemStack.hasItemMeta())
            return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta.hasDisplayName()) {
            String displayname = itemMeta.getDisplayName().replace(target, replacement);
            itemMeta.setDisplayName(displayname);
        }
        if (itemMeta.hasLore()) {
            List<String> lore = new ArrayList<>();
            List<String> current = itemMeta.getLore();
            for (String s : current) {
                lore.add(s.replace(target, replacement));
            }
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);
    }
}
