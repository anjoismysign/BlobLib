package us.mytheria.bloblib.entities.listeners;

import org.bukkit.block.Block;

public class SelPosListener extends InputListener {
    private Block input;

    public SelPosListener(String owner, long timeout, Runnable inputRunnable,
                          Runnable timeoutRunnable) {
        super(owner, timeout, inputRunnable, timeoutRunnable);
    }

    @Override
    public Block getInput() {
        return input;
    }

    public void setInput(Block input) {
        this.input = input;
        cancel();
        inputRunnable.run();
    }
}