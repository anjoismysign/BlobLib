package us.mytheria.bloblib.entities.listeners;

import java.util.function.Consumer;

public class ChatListener extends TimeoutInputListener {
    private String input;

    public ChatListener(String owner, long timeout, Consumer<ChatListener> inputConsumer,
                        Consumer<ChatListener> timeoutConsumer) {
        super(owner, timeout, inputListener -> {
            inputConsumer.accept((ChatListener) inputListener);
        }, timeoutListener -> {
            timeoutConsumer.accept((ChatListener) timeoutListener);
        });
    }

    @Override
    public String getInput() {
        return input;
    }


    public void setInput(String input) {
        this.input = input;
        cancel();
        inputConsumer.accept(this);
    }
}