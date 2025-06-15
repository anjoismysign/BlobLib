package io.github.anjoismysign.bloblib.entities.listeners;

import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class DropListener extends InputListener {
    private ItemStack input;

    public DropListener(String owner, Consumer<DropListener> inputConsumer) {
        super(owner, inputListener -> {
            inputConsumer.accept((DropListener) inputListener);
        });
    }

    @Override
    public ItemStack getInput() {
        return input;
    }


    public void setInput(ItemStack input) {
        this.input = input;
        cancel();
        inputConsumer.accept(this);
    }
}