package us.mytheria.bloblib.entities.listeners;

import org.bukkit.block.Block;

import java.util.function.Consumer;

public class SelPosListener extends TimeoutInputListener {
    private Block input;

    public SelPosListener(String owner, long timeout, Consumer<SelPosListener> inputConsumer,
                          Consumer<SelPosListener> timeoutConsumer) {
        super(owner, timeout, inputListener -> inputConsumer.accept((SelPosListener) inputListener),
                timeoutListener -> timeoutConsumer.accept((SelPosListener) timeoutListener));
    }

    @Override
    public Block getInput() {
        return input;
    }

    public void setInput(Block input) {
        this.input = input;
        cancel();
        inputConsumer.accept(this);
    }
}