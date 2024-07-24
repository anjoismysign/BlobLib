package us.mytheria.bloblib.utilities;

import org.bukkit.inventory.ItemStack;

public interface ItemStackUtilMiddleman {
    /**
     * Will display an ItemStack by either their ItemMeta's displayname or their Material's name
     *
     * @param itemStack The ItemStack to display
     * @return The displayname of the ItemStack
     */
    String display(ItemStack itemStack);

    /**
     * Will replace all instances of a string in an ItemStack's ItemMeta
     *
     * @param itemStack   The ItemStack to replace in
     * @param target      The string to replace
     * @param replacement The string to replace with
     */
    void replace(ItemStack itemStack, String target, String replacement);
}
