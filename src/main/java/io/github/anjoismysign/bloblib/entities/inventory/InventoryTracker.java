package io.github.anjoismysign.bloblib.entities.inventory;

import java.util.function.Function;

public class InventoryTracker<T extends SharableInventory<R>, R extends InventoryButton> {
    private final InventoryDataRegistry<R> registry;
    private T inventory;
    private String locale;
    private boolean isValid;
    private final Function<String, T> function;

    protected InventoryTracker(T inventory,
                               String locale,
                               InventoryDataRegistry<R> registry,
                               Function<String, T> function) {
        this.inventory = inventory;
        this.locale = locale;
        this.isValid = true;
        this.registry = registry;
        this.function = function;
    }

    public InventoryDataRegistry<R> getRegistry() {
        return registry;
    }

    public T getInventory() {
        return inventory;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
        this.inventory = function.apply(locale);
    }

    public void invalid() {
        this.isValid = false;
    }

    public boolean isValid() {
        return isValid;
    }
}
