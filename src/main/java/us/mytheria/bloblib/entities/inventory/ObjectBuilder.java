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
import java.util.UUID;
import java.util.function.Function;

public abstract class ObjectBuilder<T> extends BlobInventory {
    private final UUID builderId;
    private final HashMap<String, ObjectBuilderButton<?>> objectBuilderButtons;
    private Function<ObjectBuilder<T>, T> function;

    public ObjectBuilder(BlobInventory blobInventory, UUID builderId) {
        super(blobInventory.getTitle(), blobInventory.getSize(), blobInventory.getButtonManager());
        this.builderId = builderId;
        this.objectBuilderButtons = new HashMap<>();
    }

    /**
     * @return player matching builderId.
     * null if no player is found.
     */
    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(builderId);
    }

    public UUID getBuilderId() {
        return builderId;
    }

    public void openInventory() {
        getPlayer().openInventory(getInventory());
    }

    public void updateDefaultButton(String key, String regex, String replacement) {
        modifyDefaultButton(key, modder -> modder.replace(regex, replacement));
    }

    public void modifyDefaultButton(String key,
                                    Function<ItemStackModder, ItemStackModder> function) {
        this.getSlots(key).forEach(i -> {
            ItemStack itemStack = cloneDefaultButton(key);
            ItemStackModder modder = ItemStackModder.mod(itemStack);
            function.apply(modder);
            this.setButton(i, itemStack);
        });
    }

    public T build() {
        if (function != null)
            return function.apply(this);
        return null;
    }

    public boolean isBuildButton(int slot) {
        return getSlots("Build").contains(slot);
    }

    public boolean isObjectBuilderButton(int slot) {
        Uber<Boolean> is = new Uber<>(false);
        objectBuilderButtons.values().forEach(button -> {
            if (getSlots(button.getButtonKey()).contains(slot))
                is.talk(true);
        });
        return is.thanks();
    }

    public void ifObjectBuilderButtonAddListener(int slot, Player player,
                                                 BlobSound sound) {
        objectBuilderButtons.values().forEach(button -> {
            if (getSlots(button.getButtonKey()).contains(slot))
                button.addListener(player, Optional.ofNullable(sound));
        });
    }

    public void ifObjectBuilderButtonAddListener(int slot, Player player) {
        objectBuilderButtons.values().forEach(button -> {
            if (getSlots(button.getButtonKey()).contains(slot))
                button.addListener(player);
        });
    }

    public void handle(int slot, Player player) {
        ifObjectBuilderButtonAddListener(slot, player);
        if (isBuildButton(slot))
            build();
    }

    public ObjectBuilder<T> addObjectBuilderButton(ObjectBuilderButton<?> builderButton) {
        objectBuilderButtons.put(builderButton.getButtonKey(), builderButton);
        return this;
    }

    public ObjectBuilder<T> addQuickStringButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_STRING(buttonKey, timeout, this));
    }

    public ObjectBuilder<T> addQuickByteButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_BYTE(buttonKey, timeout, this));
    }

    public ObjectBuilder<T> addQuickShortButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_SHORT(buttonKey, timeout, this));
    }

    public ObjectBuilder<T> addQuickIntegerButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_INTEGER(buttonKey, timeout, this));
    }

    public ObjectBuilder<T> addQuickLongButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_LONG(buttonKey, timeout, this));
    }

    public ObjectBuilder<T> addQuickFloatButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_FLOAT(buttonKey, timeout, this));
    }

    public ObjectBuilder<T> addQuickDoubleButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_DOUBLE(buttonKey, timeout, this));
    }

    public ObjectBuilder<T> addQuickBlockButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_BLOCK(buttonKey, timeout, this));
    }

    public ObjectBuilder<T> addQuickItemButton(String buttonKey) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_ITEM(buttonKey, this));
    }

    public ObjectBuilder<T> addQuickSelectorButton(String buttonKey,
                                                   VariableSelector<T> selector,
                                                   Function<T, String> ifAvailable) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_SELECTOR(buttonKey, selector, ifAvailable, this));
    }

    public ObjectBuilder<T> addQuickMessageButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_MESSAGE(buttonKey, timeout, this));
    }

    public ObjectBuilder<T> addQuickWorldButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.QUICK_WORLD(buttonKey, timeout, this));
    }

    public ObjectBuilder<T> addQuickOptionalByteButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.OPTIONAL_QUICK_BYTE(buttonKey, timeout, this));
    }

    public ObjectBuilder<T> addQuickOptionalShortButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.OPTIONAL_QUICK_SHORT(buttonKey, timeout, this));
    }

    public ObjectBuilder<T> addQuickOptionalIntegerButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.OPTIONAL_QUICK_INTEGER(buttonKey, timeout, this));
    }

    public ObjectBuilder<T> addQuickOptionalLongButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.OPTIONAL_QUICK_LONG(buttonKey, timeout, this));
    }

    public ObjectBuilder<T> addQuickOptionalFloatButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.OPTIONAL_QUICK_FLOAT(buttonKey, timeout, this));
    }

    public ObjectBuilder<T> addQuickOptionalDoubleButton(String buttonKey, long timeout) {
        return addObjectBuilderButton(ObjectBuilderButtonBuilder.OPTIONAL_QUICK_DOUBLE(buttonKey, timeout, this));
    }

    public ObjectBuilder<T> setFunction(Function<ObjectBuilder<T>, T> function) {
        this.function = function;
        return this;
    }
}
