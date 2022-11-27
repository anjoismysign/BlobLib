package us.mytheria.bloblib.entities.listeners;

import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.inventory.VariableSelector;

public class SelectorListener extends InputListener {
    private Object input;
    private VariableSelector selector;

    public SelectorListener(String owner, long timeout, Runnable inputRunnable,
                            Runnable timeoutRunnable, VariableSelector selector) {
        super(owner, timeout, inputRunnable, timeoutRunnable);
        this.selector = selector;
        selector.open();
        register();
    }

    private void register() {
        BlobLib.getInstance().getSelectorManager().addSelectorListener(getPlayerOwner(), this);
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

    public VariableSelector getSelector() {
        return selector;
    }
}