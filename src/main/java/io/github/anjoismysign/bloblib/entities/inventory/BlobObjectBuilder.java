package io.github.anjoismysign.bloblib.entities.inventory;

import io.github.anjoismysign.anjo.entities.Uber;
import io.github.anjoismysign.bloblib.entities.BlobObject;
import io.github.anjoismysign.bloblib.entities.message.BlobMessage;
import io.github.anjoismysign.bloblib.entities.message.BlobSound;
import io.github.anjoismysign.bloblib.itemstack.ItemStackModder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @param <T> the type of object to build
 * @author anjoismysign
 * <p>
 * A BlobObjectBuilder is an inventory that allows the user to build an object.
 * The idea behind it is that through the Minecraft client, while connected
 * to the Minecraft server, the user can build an object by interacting with
 * a Bukkit Inventory / GUI.
 */
public abstract class BlobObjectBuilder<T extends BlobObject> extends BlobInventory {
    private final UUID builderId;
    private final HashMap<String, ObjectBuilderButton<?>> objectBuilderButtons;
    private Function<BlobObjectBuilder<T>, T> function;

    /**
     * Constructs a new ObjectBuilder.
     *
     * @param blobInventory the inventory to use
     * @param builderId     the builder's UUID
     */
    public BlobObjectBuilder(@NotNull BlobInventory blobInventory,
                             @NotNull UUID builderId) {
        super(Objects.requireNonNull(blobInventory,
                        "blobInventory cannot be null").getTitle(),
                blobInventory.getSize(), blobInventory.getButtonManager());
        this.builderId = Objects.requireNonNull(builderId,
                "builderId cannot be null");
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
        Set<Integer> slots = getSlots(key);
        if (slots == null)
            throw new NullPointerException("'" + key + "' is not a valid button key " +
                    "inside '" + getTitle() + "' inventory");
        slots.forEach(i -> {
            ItemStack itemStack = cloneDefaultButton(key);
            ItemStackModder modder = ItemStackModder.mod(itemStack);
            function.apply(modder);
            setButton(i, itemStack);
        });
    }

    /**
     * Will attempt to build the object
     * using the function. The idea is
     * that the function will
     *
     * @return the object
     */
    public T build() {
        if (function != null)
            return function.apply(this);
        return null;
    }

    public abstract T construct();

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
        for (ObjectBuilderButton<?> button : objectBuilderButtons.values()) {
            String key = button.getButtonKey();
            Set<Integer> set = getSlots(key);
            if (set == null) {
                continue;
            }
            if (getSlots(button.getButtonKey()).contains(slot)) {
                button.addListener(player);
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
        boolean isObjectBuilderButton = ifObjectBuilderButtonAddListener(slot, player);
        if (isObjectBuilderButton)
            return;
        if (isBuildButton(slot))
            build();
    }

    /**
     * Adds an object builder button.
     *
     * @param builderButton the button
     * @return this
     */
    public BlobObjectBuilder<T> addObjectBuilderButton(ObjectBuilderButton<?> builderButton) {
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
    public BlobObjectBuilder<T> addQuickStringButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_STRING(buttonKey, timeout, this));
    }

    /**
     * Add a quick byte button. Accepts negative values.
     * If input equalsIgnoreCase 'null', button's value will be set null.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public BlobObjectBuilder<T> addQuickByteButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_BYTE(buttonKey, timeout, this));
    }

    /**
     * Add a quick short button. Accepts negative values.
     * If input equalsIgnoreCase 'null', button's value will be set null.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public BlobObjectBuilder<T> addQuickShortButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_SHORT(buttonKey, timeout, this));
    }

    /**
     * Add a quick integer button. Accepts negative values.
     * If input equalsIgnoreCase 'null', button's value will be set null.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public BlobObjectBuilder<T> addQuickIntegerButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_INTEGER(buttonKey, timeout, this));
    }

    /**
     * Add a quick long button. Accepts negative values.
     * If input equalsIgnoreCase 'null', button's value will be set null.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public BlobObjectBuilder<T> addQuickLongButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_LONG(buttonKey, timeout, this));
    }

    /**
     * Add a quick float button. Accepts negative values.
     * If input equalsIgnoreCase 'null', button's value will be set null.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public BlobObjectBuilder<T> addQuickFloatButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_FLOAT(buttonKey, timeout, this));
    }

    /**
     * Add a quick double button. Accepts negative values.
     * If input equalsIgnoreCase 'null', button's value will be set null.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public BlobObjectBuilder<T> addQuickDoubleButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_DOUBLE(buttonKey, timeout, this));
    }

    /**
     * Add a quick block button.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public BlobObjectBuilder<T> addQuickBlockButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_BLOCK(buttonKey, timeout, this));
    }

    /**
     * Add a quick block button.
     * Will use the block above selection.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public BlobObjectBuilder<T> addQuickAboveBlockButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_ABOVE_BLOCK(buttonKey, timeout, this));
    }

    /**
     * Add a quick item button.
     *
     * @param buttonKey the button key
     * @return this
     */
    public BlobObjectBuilder<T> addQuickItemButton(String buttonKey) {
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
    public BlobObjectBuilder<T> addQuickSelectorButton(String buttonKey,
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
    public BlobObjectBuilder<T> addQuickMessageButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_MESSAGE(buttonKey, timeout, this));
    }

    /**
     * Add a quick world button.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public BlobObjectBuilder<T> addQuickWorldButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_WORLD(buttonKey, timeout, this));
    }

    /**
     * Add a quick action block button which accepts a consumer when input is given.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @param consumer  the consumer (which is of Block type)
     * @return The button
     */
    public BlobObjectBuilder<T> addQuickActionBlockButton(String buttonKey, long timeout, Consumer<Block> consumer) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_ACTION_BLOCK(
                buttonKey, timeout, this, consumer));
    }

    /**
     * Add a quick action item button which accepts a consumer when input is given.
     *
     * @param buttonKey the button key
     * @param consumer  the consumer (which is of ItemStack type)
     * @return The button
     */
    public BlobObjectBuilder<T> addQuickActionItemButton(String buttonKey, Consumer<ItemStack> consumer) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_ACTION_ITEM(
                buttonKey, this, consumer));
    }

    /**
     * Add a quick action selector button which accepts a consumer when input is given.
     *
     * @param buttonKey   The key of the button
     * @param selector    The selector
     * @param ifAvailable The if available
     * @param consumer    The consumer (which is of T type)
     * @return The button
     */
    public BlobObjectBuilder<T> addQuickActionSelectorButton(String buttonKey,
                                                             VariableSelector<T> selector,
                                                             Function<T, String> ifAvailable,
                                                             Consumer<T> consumer) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_ACTION_SELECTOR(
                buttonKey, selector, ifAvailable, this, consumer));
    }

    /**
     * Add a quick world button which accepts a consumer when input is given.
     *
     * @param buttonKey The key of the button
     * @param timeout   The timeout
     * @param consumer  The consumer (which is a ReferenceBlobMessage)
     * @return The button
     */
    public BlobObjectBuilder<T> addQuickActionMessageButton(String buttonKey, long timeout, Consumer<BlobMessage> consumer) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_ACTION_MESSAGE(
                buttonKey, timeout, this, consumer));
    }

    /**
     * A quick ObjectBuilderButton for Worlds that accepts
     * a consumer when input is given.
     *
     * @param buttonKey The key of the button
     * @param timeout   The timeout
     * @param consumer  The consumer (which is a World)
     * @return The button
     */
    public BlobObjectBuilder<T> addQuickActionWorldButton(String buttonKey, long timeout, Consumer<World> consumer) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_ACTION_WORLD(
                buttonKey, timeout, this, consumer));
    }


    /**
     * Add a positive byte button.
     * if input is lower than 0, button
     * will be empty.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public BlobObjectBuilder<T> addPositiveByteButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.POSITIVE_BYTE(buttonKey, timeout, this));
    }

    /**
     * Add a positive short button.
     * if input is lower than 0, button
     * will be empty.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public BlobObjectBuilder<T> addPositiveShortButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.POSITIVE_SHORT(buttonKey, timeout, this));
    }

    /**
     * Add a positive integer button.
     * if input is lower than 0, button
     * will be empty.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public BlobObjectBuilder<T> addPositiveIntegerButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.POSITIVE_INTEGER(buttonKey, timeout, this));
    }

    /**
     * Add a positive long button.
     * if input is lower than 0, button
     * will be empty.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public BlobObjectBuilder<T> addPositiveLongButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.POSITIVE_LONG(buttonKey, timeout, this));
    }

    /**
     * Add a positive float button.
     * if input is lower than 0, button
     * will be empty.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public BlobObjectBuilder<T> addPositiveFloatButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.POSITIVE_FLOAT(buttonKey, timeout, this));
    }

    /**
     * Add a positive double button.
     * if input is lower than 0, button
     * will be empty.
     *
     * @param buttonKey the button key
     * @param timeout   the timeout
     * @return this
     */
    public BlobObjectBuilder<T> addPositiveDoubleButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.POSITIVE_DOUBLE(buttonKey, timeout, this));
    }

    /**
     * A quick navigator for booleans.
     * Value cannot be empty nor null.
     * By default, the value is false.
     *
     * @param buttonKey The key of the button
     * @return The button
     */
    public BlobObjectBuilder<T> addBoolean(String buttonKey) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.BOOLEAN(buttonKey, this));
    }

    /**
     * A quick navigator for booleans.
     * Value cannot be empty nor null.
     * By default, the value is true.
     *
     * @param buttonKey The key of the button
     * @return The button
     */
    public BlobObjectBuilder<T> addBooleanDefaultTrue(String buttonKey) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.BOOLEAN_DEFAULT_TRUE(buttonKey, this));
    }

    /**
     * A quick navigator for any type of array.
     * Value cannot be empty nor null.
     * By default, the value is the first element of the array.
     *
     * @param buttonKey The key of the button
     * @param enumClass The enum class
     * @return The button
     */
    public <E extends Enum<E>> BlobObjectBuilder<T> addEnumNavigator(String buttonKey, Class<E> enumClass) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.ENUM_NAVIGATOR(buttonKey, enumClass, this));
    }

    /**
     * Set the function to be called when the build button is clicked
     *
     * @param function the function to be called
     * @return this
     */
    public BlobObjectBuilder<T> setFunction(Function<BlobObjectBuilder<T>, T> function) {
        this.function = function;
        return this;
    }
}
