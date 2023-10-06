package us.mytheria.bloblib.entities.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A registry for InventoryBuilderCarrier objects.
 * Holds a default locale and a map of locales to InventoryBuilderCarrier objects.
 * It also holds a ClickEvent object consumed when a button is clicked.
 *
 * @param <T> the type of InventoryButton
 */
public class InventoryDataRegistry<T extends InventoryButton> {
    @NotNull
    private final String defaultLocale, key;
    private final Map<String, InventoryBuilderCarrier<T>> carriers;
    private final Map<String, Consumer<InventoryClickEvent>> clickEvents;

    /**
     * Will instantiate a new InventoryDataRegistry with the specified default locale.
     *
     * @param defaultLocale the default locale
     * @param <T>           the type of InventoryButton
     * @return a new InventoryDataRegistry with the specified default locale
     */
    public static <T extends InventoryButton> InventoryDataRegistry<T> of(
            @NotNull String defaultLocale, @NotNull String key) {
        Objects.requireNonNull(defaultLocale, "'defaultLocale' cannot be null!");
        Objects.requireNonNull(key, "'key' cannot be null!");
        return new InventoryDataRegistry<>(defaultLocale, key);
    }

    private InventoryDataRegistry(@NotNull String defaultLocale, @NotNull String key) {
        this.defaultLocale = defaultLocale;
        this.key = key;
        this.carriers = new HashMap<>();
        this.clickEvents = new HashMap<>();
    }

    /**
     * Will process the carrier and add it to the registry.
     *
     * @param carrier the carrier to process
     * @return true if the carrier was added, false if it already exists
     */
    public boolean process(@NotNull InventoryBuilderCarrier<T> carrier) {
        Objects.requireNonNull(carrier, "carrier cannot be null");
        String locale = carrier.locale();
        if (this.carriers.containsKey(locale))
            return false;
        this.carriers.put(carrier.locale(), carrier);
        return true;
    }

    /**
     * Will get the carrier for the specified locale, or the default if it doesn't exist.
     *
     * @param locale the locale to get the carrier for
     * @return the carrier for the specified locale, or the default if it doesn't exist
     */
    @NotNull
    public InventoryBuilderCarrier<T> get(String locale) {
        InventoryBuilderCarrier<T> result = this.carriers.get(locale);
        if (result == null)
            result = getDefault();
        return result;
    }

    /**
     * Will get the default carrier.
     *
     * @return the default carrier
     */
    @NotNull
    public InventoryBuilderCarrier<T> getDefault() {
        return this.carriers.get(this.defaultLocale);
    }

    @NotNull
    public String getKey() {
        return key;
    }

    /**
     * Will set the click event for the specified button.
     *
     * @param button the button to add the click event for
     * @param event  the click event
     */
    public void onClick(String button, Consumer<InventoryClickEvent> event) {
        this.clickEvents.put(button, event);
    }

    /**
     * Will process the click event for the specified button.
     *
     * @param button the button to process the click event for
     * @param event  the click event
     */
    public void processClickEvent(String button, InventoryClickEvent event) {
        Consumer<InventoryClickEvent> clickEvent = this.clickEvents.get(button);
        if (clickEvent == null)
            return;
        clickEvent.accept(event);
    }
}
