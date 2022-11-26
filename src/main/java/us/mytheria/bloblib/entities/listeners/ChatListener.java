package us.mytheria.bloblib.entities.listeners;

public class ChatListener extends InputListener {
    private String input;

    public ChatListener(String owner, long timeout, Runnable inputRunnable,
                        Runnable timeoutRunnable) {
        super(owner, timeout, inputRunnable, timeoutRunnable);
    }

    @Override
    public String getInput() {
        return input;
    }


    public void setInput(String input) {
        this.input = input;
        cancel();
        inputRunnable.run();
    }
}