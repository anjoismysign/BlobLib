package us.mytheria.bloblib.itemstack;

import me.anjoismysign.utilities.ItemStackUtil;
import org.bukkit.inventory.ItemStack;
import us.mytheria.bloblib.utilities.ItemStackUtilMiddleman;

public class Util1_20_5 implements ItemStackUtilMiddleman {
    private static Util1_20_5 instance;

    public static Util1_20_5 getInstance() {
        if (instance == null) {
            instance = new Util1_20_5();
        }
        return instance;
    }

    public String display(ItemStack itemStack) {
        return ItemStackUtil.display(itemStack);
    }

    public void replace(ItemStack itemStack, String target, String replacement) {
        ItemStackUtil.replace(itemStack, target, replacement);
    }
}
