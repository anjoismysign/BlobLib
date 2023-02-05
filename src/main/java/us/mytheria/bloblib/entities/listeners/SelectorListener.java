package us.mytheria.bloblib.entities.listeners;

import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.inventory.VariableSelector;

import java.util.function.Consumer;

public class SelectorListener<T> extends InputListener {
    private T input;
    private final VariableSelector<T> selector;

    public SelectorListener(String owner, Runnable inputRunnable, VariableSelector<T> selector) {
        super(owner, inputListener -> {
            inputRunnable.run();
        });
        this.selector = selector;
        register();
        selector.open();
    }

    @SuppressWarnings("unchecked")
    public SelectorListener(String owner, Consumer<SelectorListener<T>> inputConsumer,
                            VariableSelector<T> selector) {
        super(owner, inputListener -> {
            inputConsumer.accept((SelectorListener<T>) inputListener);
        });
        this.selector = selector;
        register();
        selector.open();
    }

    private void register() {
        BlobLib.getInstance().getVariableSelectorManager().addVariableSelector(selector);
    }

    @Override
    public T getInput() {
        return input;
    }

    public void setInput(T input) {
        this.input = input;
        cancel();
        inputConsumer.accept(this);
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