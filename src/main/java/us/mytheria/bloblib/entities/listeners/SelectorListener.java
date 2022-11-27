package us.mytheria.bloblib.entities.listeners;

import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.inventory.VariableSelector;

public class SelectorListener extends InputListener {
    private Object input;
    private VariableSelector selector;

    public SelectorListener(String owner, Runnable inputRunnable, VariableSelector selector) {
        super(owner, inputRunnable);
        this.selector = selector;
        register();
        selector.open();
    }

    private void register() {
        BlobLib.getInstance().getInventoryManager().addVariableSelector(selector);
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