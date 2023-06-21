package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
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

    /**
     * @param buttonKey          The key/reference of the button inside the Inventory
     * @param defaultValue       The default value of the button
     * @param listenerBiConsumer The listener that will be called when the button is clicked
     * @param function           The function that will be called when the listener is finished
     */
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

    @Nullable
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

    public boolean isValuePresentAndNotNull(boolean debug) {
        if (debug)
            Bukkit.getLogger().info(buttonKey + ": value is present: " + value.isPresent() + " and value is not null: " + (value.get() != null));
        return value.isPresent() && value.get() != null;
    }

    public boolean isValuePresentAndNotNull() {
        return isValuePresentAndNotNull(false);
    }
}