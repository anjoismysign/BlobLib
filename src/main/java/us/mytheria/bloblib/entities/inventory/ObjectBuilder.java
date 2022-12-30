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

    public ObjectBuilder<T> setFunction(Function<ObjectBuilder<T>, T> function) {
        this.function = function;
        return this;
    }
}
