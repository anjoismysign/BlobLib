package us.mytheria.bloblib.entities.inventory;

import org.bukkit.entity.Player;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.message.BlobSound;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class ObjectBuilderButton<T> {
    private final String buttonKey;
    private Optional<T> value;
    private final BiConsumer<ObjectBuilderButton<T>, Player> listenerBiConsumer;
    private final Function<T, Boolean> function;

    protected ObjectBuilderButton(String buttonKey, Optional<T> defaultValue,
                                  BiConsumer<ObjectBuilderButton<T>, Player> listenerBiConsumer,
                                  Function<T, Boolean> function) {
        this.function = function;
        this.value = defaultValue;
        this.buttonKey = buttonKey;
        this.listenerBiConsumer = listenerBiConsumer;
    }

    public String getButtonKey() {
        return buttonKey;
    }

    public Optional<T> get() {
        return value;
    }

    public T orNull() {
        return value.orElse(null);
    }

    public void set(T newValue) {
        boolean success = function.apply(newValue);
        if (success)
            this.value = Optional.ofNullable(newValue);
    }

    public void addListener(Player player, Optional<BlobSound> clickSound) {
        player.closeInventory();
        listenerBiConsumer.accept(this, player);
        clickSound.ifPresent(blobSound -> blobSound.play(player));
    }

    public void addListener(Player player) {
        addListener(player, Optional.of(BlobLibAssetAPI.getSound("Builder.Button-Click")));
    }

    public boolean isValuePresent() {
        return value.isPresent();
    }

    public boolean isValuePresentAndNotNull() {
        return value.isPresent() && value.get() != null;
    }
}