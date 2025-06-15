package io.github.anjoismysign.bloblib.entities.inventory;

import io.github.anjoismysign.bloblib.action.Action;
import io.github.anjoismysign.bloblib.action.ActionMemo;
import io.github.anjoismysign.bloblib.action.ActionType;
import io.github.anjoismysign.bloblib.api.BlobLibActionAPI;
import io.github.anjoismysign.bloblib.api.BlobLibEconomyAPI;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableItem;
import io.github.anjoismysign.bloblib.vault.multieconomy.ElasticEconomy;
import net.milkbowl.vault.economy.IdentityEconomy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class InventoryButton {
    @NotNull
    private final String key;
    @NotNull
    private final Set<Integer> slots;
    private final boolean isPermissionInverted;
    @Nullable
    private final String hasPermission;
    private final boolean isMoneyInverted;
    private final double hasMoney;
    private final boolean isTranslatableItemInverted;
    @Nullable
    private final String hasTranslatableItem;
    @Nullable
    private final String priceCurrency;
    @NotNull
    private final List<ActionMemo> actions;

    public InventoryButton(@NotNull String key,
                           @NotNull Set<Integer> slots,
                           @Nullable String hasPermission,
                           double hasMoney,
                           @Nullable String priceCurrency,
                           @NotNull List<ActionMemo> actions,
                           @Nullable String hasTranslatableItem,
                           boolean isPermissionInverted,
                           boolean isMoneyInverted,
                           boolean isTranslatableItemInverted) {
        this.key = key;
        this.slots = slots;
        this.hasPermission = hasPermission;
        this.hasMoney = hasMoney;
        this.priceCurrency = priceCurrency;
        this.actions = actions;
        this.hasTranslatableItem = hasTranslatableItem;
        this.isPermissionInverted = isPermissionInverted;
        this.isMoneyInverted = isMoneyInverted;
        this.isTranslatableItemInverted = isTranslatableItemInverted;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @NotNull
    public Set<Integer> getSlots() {
        return slots;
    }

    @Nullable
    public String getHasPermission() {
        return hasPermission;
    }

    public double getHasMoney() {
        return hasMoney;
    }

    @Nullable
    public String getHasTranslatableItem() {
        return hasTranslatableItem;
    }

    public boolean isPermissionInverted() {
        return isPermissionInverted;
    }

    public boolean isMoneyInverted() {
        return isMoneyInverted;
    }

    public boolean isTranslatableItemInverted() {
        return isTranslatableItemInverted;
    }

    @Nullable
    public String getPriceCurrency() {
        return priceCurrency;
    }

    public boolean priceUseCustomCurrency() {
        return priceCurrency != null;
    }

    public void setDisplay(ItemStack display, ButtonManager<?> manager) {
        slots.forEach(slot -> manager.getIntegerKeys().put(slot, display));
    }

    public void setDisplay(ItemStack display, SharableInventory<?> inventory) {
        slots.forEach(slot -> {
            inventory.getButtonManager().getIntegerKeys().put(slot, display);
            inventory.setButton(slot, display);
        });
    }

    public boolean containsSlot(int slot) {
        return slots.contains(slot);
    }

    /**
     * Will handle the permission of the button.
     * If permissible has permission, it will return true.
     * If permissible does not have permission, it will return false.
     *
     * @param permissible The permissible to handle the permission for.
     * @return Whether the permission was handled successfully.
     */
    public boolean handlePermission(@NotNull Permissible permissible) {
        String permission = getHasPermission();
        if (permission == null)
            return true;
        return isPermissionInverted() != permissible.hasPermission(permission);
    }

    /**
     * Will handle the payment of the button.
     * If the price is 1E-12 or less, it will return true.
     * If the player does not have enough money, it will return false.
     * If the player has enough money, it will withdraw the money and return true.
     * Currently, doesn't support custom currencies since the API for it
     * is still under development.
     *
     * @param player The player to handle the payment for.
     * @return Whether the payment was handled successfully.
     */
    public boolean handleMoney(@NotNull Player player) {
        double price = getHasMoney();
        if (Double.compare(price, 0D) <= 0 && !isMoneyInverted())
            return true;
        ElasticEconomy elasticEconomy = BlobLibEconomyAPI.getInstance().getElasticEconomy();
        IdentityEconomy economy = elasticEconomy
                .map(Optional.ofNullable(getPriceCurrency()));
        boolean hasAmount = economy.has(player.getUniqueId(), price);
        boolean proceed = isMoneyInverted() != hasAmount;
        if (!proceed)
            return false;
        economy.withdraw(player.getUniqueId(), price);
        return true;
    }

    public boolean handleTranslatableItem(@NotNull Player player) {
        String key = getHasTranslatableItem();
        if (key == null)
            return true;
        TranslatableItem item = TranslatableItem.by(key);
        if (item == null)
            return false;
        boolean hasItem = false;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null)
                continue;
            TranslatableItem match = TranslatableItem.byItemStack(itemStack);
            if (match == null)
                continue;
            if (match.identifier().equals(item.identifier())) {
                hasItem = true;
                break;
            }
        }
        return isTranslatableItemInverted() != hasItem;
    }

    /**
     * Will handle permission, money and TranslatableItem of the button.
     * If handled successfully, it will handle the action.
     *
     * @param player The player to handle for.
     * @return Whether it was handled successfully.
     */
    public boolean handleAll(Player player) {
        boolean proceed = handlePermission(player) &&
                handleMoney(player) &&
                handleTranslatableItem(player);
        if (!proceed)
            return false;
        handleAction(player);
        return true;
    }

    /**
     * @return The actions of the button.
     */
    @NotNull
    public List<ActionMemo> getActions() {
        return actions;
    }

    /**
     * Will handle the action of the button.
     *
     * @param entity The entity to handle the action for.
     */
    public void handleAction(Entity entity) {
        if (actions.isEmpty())
            return;
        actions.forEach(memo -> {
            ActionType actionType = memo.getActionType();
            if (actionType != null)
                memo.getAction().perform(entity);
            else {
                String reference = memo.getReference();
                Action<Entity> fetch = BlobLibActionAPI.getInstance().getAction(reference);
                fetch.perform(entity);
            }
        });
    }

    public void accept(ButtonVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Will clone/copy the button to a new instance
     *
     * @return The new instance of the button.
     */
    public InventoryButton copy() {
        return new InventoryButton(key, new HashSet<>(slots),
                hasPermission, hasMoney, priceCurrency, actions,
                hasTranslatableItem, isPermissionInverted, isMoneyInverted, isTranslatableItemInverted);
    }
}
