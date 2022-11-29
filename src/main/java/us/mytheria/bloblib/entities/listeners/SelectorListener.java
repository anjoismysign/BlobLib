package us.mytheria.bloblib.entities.listeners;

import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.inventory.VariableSelector;

public class SelectorListener<T> extends InputListener {
    private T input;
    private final VariableSelector<T> selector;

    public SelectorListener(String owner, Runnable inputRunnable, VariableSelector<T> selector) {
        super(owner, inputRunnable);
        this.selector = selector;
        register();
        selector.open();
    }

    private void register() {
        BlobLib.getInstance().getInventoryManager().addVariableSelector(selector);
    }

    @Override
    public T getInput() {
        return input;
    }


    public void setInput(T input) {
        this.input = input;
        cancel();
        inputRunnable.run();
    }

    @SuppressWarnings("unchecked")
    public void setInputFromSlot(VariableSelector<?> selector, int slot) {
        if (!getSelector().equals(this.selector))
            return;
        setInput((T) selector.getValue(slot));
    }

    public VariableSelector<T> getSelector() {
        return selector;
    }
}