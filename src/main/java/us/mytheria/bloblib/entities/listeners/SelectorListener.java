package us.mytheria.bloblib.entities.listeners;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.api.BlobLibSoundAPI;
import us.mytheria.bloblib.entities.inventory.VariableSelector;
import us.mytheria.bloblib.entities.message.BlobSound;

import java.util.Objects;
import java.util.function.Consumer;

public class SelectorListener<T> extends InputListener {
    private T input;
    private final VariableSelector<T> selector;
    @NotNull
    private String clickSoundReference;

    public SelectorListener(String owner, Runnable inputRunnable, VariableSelector<T> selector) {
        super(owner, inputListener -> {
            inputRunnable.run();
        });
        this.selector = selector;
        register();
        selector.open();
        clickSoundReference = "Builder.Button-Click";
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
        clickSoundReference = "Builder.Button-Click";
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

    /**
     * Sets the input from the specified slot in the {@link VariableSelector}.
     * <p>
     * This method will only succeed if the provided selector matches the one associated with this listener.
     * If no value is available at the specified slot, or if setting the new input fails for any reason,
     * the method returns <code>false</code>.
     *
     * @param selector the selector from which to retrieve the input
     * @param slot     the index of the slot containing the desired input
     * @return <code>true</code> if the input was successfully set, or <code>false</code> otherwise
     */
    @SuppressWarnings("unchecked")
    public boolean setInputFromSlot(VariableSelector<?> selector, int slot) {
        if (!getSelector().equals(this.selector))
            return false;
        @Nullable T input = (T) selector.getValue(slot);
        if (input == null)
            return false;
        setInput(input);
        return true;
    }

    public VariableSelector<T> getSelector() {
        return selector;
    }

    @NotNull
    public String getClickSoundReference() {
        return clickSoundReference;
    }

    public void setClickSoundReference(@NotNull String clickSoundReference) {
        Objects.requireNonNull(clickSoundReference, "'blobSoundReference' cannot be null");
        this.clickSoundReference = clickSoundReference;
    }

    @NotNull
    public BlobSound getClickSound() {
        return Objects.requireNonNull(BlobLibSoundAPI.getInstance().getSound(clickSoundReference), "Not a valid BlobSound: " + clickSoundReference);
    }
}