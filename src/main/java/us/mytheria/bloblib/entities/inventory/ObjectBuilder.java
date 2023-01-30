package us.mytheria.bloblib.entities.inventory;

import me.anjoismysign.anjo.entities.Uber;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.mytheria.bloblib.entities.message.BlobSound;
import us.mytheria.bloblib.itemstack.ItemStackModder;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * @param <T> the type of object to build
 * @author anjoismysign
 * <p>
 * An ObjectBuilder is an inventory that allows the user to build an object.
 * The idea behind it is that through the Minecraft client, while connected
 * to the Minecraft server, the user can build an object by interacting with
 * a Bukkit Inventory / GUI.
 */
public abstract class ObjectBuilder<T> extends BlobInventory {
    private final UUID builderId;
    private final HashMap<String, ObjectBuilderButton<?>> objectBuilderButtons;
    private Function<ObjectBuilder<T>, T> function;

    /**
     * Constructs a new ObjectBuilder.
     *
     * @param blobInventory the inventory to use
     * @param builderId     the builder's UUID
     */
    public ObjectBuilder(BlobInventory blobInventory, UUID builderId) {
        super(blobInventory.getTitle(), blobInventory.getSize(), blobInventory.getButtonManager());
        this.builderId = builderId;
        this.objectBuilderButtons = new HashMap<>();
    }

    /**
     * Retrieves an ObjectBuilderButton.
     *
     * @param key the key that points to the button
     * @return the ObjectBuilderButton
     */
    public ObjectBuilderButton<?> getObjectBuilderButton(String key) {
        return objectBuilderButtons.get(key);
    }

    /**
     * @return player matching builderId.
     * null if no player is found.
     */
    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(builderId);
    }

    /**
     * Retrieves builder's UUID
     *
     * @return builder's UUID
     */
    public UUID getBuilderId() {
        return builderId;
    }

    /**
     * Opens the inventory for the player.
     */
    public void openInventory() {
        getPlayer().openInventory(getInventory());
    }

    /**
     * Updates a default button.
     *
     * @param key         the key of the button
     * @param regex       the regex to replace
     * @param replacement the replacement
     */
    public void updateDefaultButton(String key, String regex, String replacement) {
        modifyDefaultButton(key, modder -> modder.replace(regex, replacement));
    }

    /**
     * Modifies a default button.
     *
     * @param key      the key of the button
     * @param function the function to modify the button
     */
    public void modifyDefaultButton(String key,
                                    Function<ItemStackModder, ItemStackModder> function) {
        this.getSlots(key).forEach(i -> {
            ItemStack itemStack = cloneDefaultButton(key);
            ItemStackModder modder = ItemStackModder.mod(itemStack);
            function.apply(modder);
            this.setButton(i, itemStack);
        });
    }

    /**
     * Will attempt to build the object.
     *
     * @return the object
     */
    public T build() {
        if (function != null)
            return function.apply(this);
        return null;
    }

    /**
     * Checks if it's a build button.
     *
     * @param slot the slot to check
     * @return true if it's a build button
     */
    public boolean isBuildButton(int slot) {
        return getSlots("Build").contains(slot);
    }

    /**
     * Checks if it's an ObjectBuilderButton.
     *
     * @param slot the slot to check
     * @return true if it's an ObjectBuilderButton
     */
    public boolean isObjectBuilderButton(int slot) {
        Uber<Boolean> is = new Uber<>(false);
        objectBuilderButtons.values().forEach(button -> {
            if (getSlots(button.getButtonKey()).contains(slot))
                is.talk(true);
        });
        return is.thanks();
    }

    /**
     * If the slot is an object builder button, adds the listener.
     *
     * @param slot   the slot
     * @param player the player
     * @param sound  the sound
     */
    public void ifObjectBuilderButtonAddListener(int slot, Player player,
                                                 BlobSound sound) {
        objectBuilderButtons.values().forEach(button -> {
            if (getSlots(button.getButtonKey()).contains(slot))
                button.addListener(player, Optional.ofNullable(sound));
        });
    }

    /**
     * If the slot is an object builder button, adds the listener.
     *
     * @param slot   the slot
     * @param player the player
     */
    public boolean ifObjectBuilderButtonAddListener(int slot, Player player) {
        Logger logger = Bukkit.getLogger();
        for (ObjectBuilderButton<?> button : objectBuilderButtons.values()) {
            String key = button.getButtonKey();
            logger.info("button.getButtonKey() = " + key);
            Set<Integer> set = getSlots(key);
            if (set == null) {
                logger.info("set is null: " + key);
                continue;
            }
            if (getSlots(button.getButtonKey()).contains(slot)) {
                button.addListener(player, Optional.empty());
                return true;
            }
        }
        return false;
    }

    /**
     * Handles all buttons, including build button.
     * Used in InventoryClickEvent, slot parameter
     * should be event.getRawSlot().
     *
     * @param slot   the slot
     * @param player the player
     */
    public void handle(int slot, Player player) {
        Logger logger = Bukkit.getLogger();
        logger.info("handling slot " + slot);
        boolean isObjectBuilderButton = ifObjectBuilderButtonAddListener(slot, player);
        if (isObjectBuilderButton) {
            logger.info("Is object builder button");
            return;
        }
        if (isBuildButton(slot)) {
            build();
            logger.info("Is build button");
            return;
        }
        logger.info("Couldn't handle");
    }

    /**
     * Adds an object builder button.
     *
     * @param builderButton the button
     * @return this
     */
    public ObjectBuilder<T> addObjectBuilderButton(ObjectBuilderButton<?> builderButton) {
        objectBuilderButtons.put(builderButton.getButtonKey(), builderButton);
        return this;
    }

    /**
     * Add a quick String button.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public ObjectBuilder<T> addQuickStringButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_STRING(buttonKey, timeout, this));
    }

    /**
     * Add a quick byte button.
     * accepts negative values.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public ObjectBuilder<T> addQuickByteButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_BYTE(buttonKey, timeout, this));
    }

    /**
     * Add a quick short button.
     * accepts negative values.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public ObjectBuilder<T> addQuickShortButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_SHORT(buttonKey, timeout, this));
    }

    /**
     * Add a quick integer button.
     * accepts negative values.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public ObjectBuilder<T> addQuickIntegerButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_INTEGER(buttonKey, timeout, this));
    }

    /**
     * Add a quick long button.
     * accepts negative values.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public ObjectBuilder<T> addQuickLongButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_LONG(buttonKey, timeout, this));
    }

    /**
     * Add a quick float button.
     * accepts negative values.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public ObjectBuilder<T> addQuickFloatButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_FLOAT(buttonKey, timeout, this));
    }

    /**
     * Add a quick double button.
     * accepts negative values.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public ObjectBuilder<T> addQuickDoubleButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_DOUBLE(buttonKey, timeout, this));
    }

    /**
     * Add a quick block button.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public ObjectBuilder<T> addQuickBlockButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_BLOCK(buttonKey, timeout, this));
    }

    /**
     * Add a quick item button.
     *
     * @param buttonKey the button key
     * @return this
     */
    public ObjectBuilder<T> addQuickItemButton(String buttonKey) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_ITEM(buttonKey, this));
    }

    /**
     * Add a quick selector button.
     *
     * @param buttonKey   the button key
     * @param selector    the selector
     * @param ifAvailable the if available
     * @return this
     */
    public ObjectBuilder<T> addQuickSelectorButton(String buttonKey,
                                                   VariableSelector<T> selector,
                                                   Function<T, String> ifAvailable) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_SELECTOR(buttonKey, selector, ifAvailable, this));
    }

    /**
     * Add a quick message button.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public ObjectBuilder<T> addQuickMessageButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_MESSAGE(buttonKey, timeout, this));
    }

    /**
     * Add a quick world button.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public ObjectBuilder<T> addQuickWorldButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_WORLD(buttonKey, timeout, this));
    }

    /**
     * Add a quick optional byte button.
     * if input is lower than 0, button
     * will be empty.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public ObjectBuilder<T> addQuickOptionalByteButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.OPTIONAL_QUICK_BYTE(buttonKey, timeout, this));
    }

    /**
     * Add a quick optional short button.
     * if input is lower than 0, button
     * will be empty.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public ObjectBuilder<T> addQuickOptionalShortButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.OPTIONAL_QUICK_SHORT(buttonKey, timeout, this));
    }

    /**
     * Add a quick optional integer button.
     * if input is lower than 0, button
     * will be empty.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public ObjectBuilder<T> addQuickOptionalIntegerButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.OPTIONAL_QUICK_INTEGER(buttonKey, timeout, this));
    }

    /**
     * Add a quick optional long button.
     * if input is lower than 0, button
     * will be empty.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public ObjectBuilder<T> addQuickOptionalLongButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.OPTIONAL_QUICK_LONG(buttonKey, timeout, this));
    }

    /**
     * Add a quick optional float button.
     * if input is lower than 0, button
     * will be empty.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public ObjectBuilder<T> addQuickOptionalFloatButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.OPTIONAL_QUICK_FLOAT(buttonKey, timeout, this));
    }

    /**
     * Add a quick optional double button.
     * if input is lower than 0, button
     * will be empty.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public ObjectBuilder<T> addQuickOptionalDoubleButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.OPTIONAL_QUICK_DOUBLE(buttonKey, timeout, this));
    }

    /**
     * Set the function to be called when the build button is clicked
     *
     * @param function the function to be called
     * @return this
     */
    public ObjectBuilder<T> setFunction(Function<ObjectBuilder<T>, T> function) {
        this.function = function;
        return this;
    }
}
