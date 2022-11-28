package us.mytheria.bloblib.entities.listeners;

import org.bukkit.inventory.ItemStack;

public class DropListener extends InputListener {
    private ItemStack input;

    public DropListener(String owner, Runnable inputRunnable) {
        super(owner, inputRunnable);
    }

    @Override
    public ItemStack getInput() {
        return input;
    }


    public void setInput(ItemStack input) {
        this.input = input;
        cancel();
        inputRunnable.run();
    }
}