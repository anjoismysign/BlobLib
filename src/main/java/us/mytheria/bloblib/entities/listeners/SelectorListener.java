package us.mytheria.bloblib.entities.listeners;

public class SelectorListener extends InputListener {
    private Object input;

    public SelectorListener(String owner, long timeout, Runnable inputRunnable,
                            Runnable timeoutRunnable) {
        super(owner, timeout, inputRunnable, timeoutRunnable);
    }

    @Override
    public Object getInput() {
        return input;
    }

    public void setInput(Object input) {
        this.input = input;
        cancel();
        inputRunnable.run();
    }
}