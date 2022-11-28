package us.mytheria.bloblib.entities.listeners;

import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.inventory.VariableSelector;

public class SelectorListener<E> extends InputListener {
    private E input;
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
    public E getInput() {
        return input;
    }


    public void setInput(E input) {
        this.input = input;
        cancel();
        inputRunnable.run();
    }

    public VariableSelector getSelector() {
        return selector;
    }
}