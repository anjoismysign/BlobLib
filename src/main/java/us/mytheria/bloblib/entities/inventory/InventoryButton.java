package us.mytheria.bloblib.entities.inventory;

import net.milkbowl.vault.economy.IdentityEconomy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLibAPI;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.action.Action;
import us.mytheria.bloblib.vault.multieconomy.ElasticEconomy;

import java.util.Optional;
import java.util.Set;

public class InventoryButton {
    private final String key;
    private final Set<Integer> slots;
    private final String permission;
    private final double price;
    private final String priceCurrency;
    private final String action;

    public InventoryButton(String key, Set<Integer> slots,
                           @Nullable String permission,
                           double price, @Nullable String priceCurrency,
                           @Nullable String action) {
        this.key = key;
        this.slots = slots;
        this.permission = permission;
        this.price = price;
        this.priceCurrency = priceCurrency;
        this.action = action;
    }

    public String getKey() {
        return key;
    }

    public Set<Integer> getSlots() {
        return slots;
    }

    @Nullable
    public String getPermission() {
        return permission;
    }

    public boolean requiresPermission() {
        return permission != null;
    }

    public double getPrice() {
        return price;
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
    public boolean handlePermission(Permissible permissible) {
        if (!requiresPermission())
            return true;
        return permissible.hasPermission(getPermission());
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
    public boolean handlePayment(Player player) {
        double price = getPrice();
        if (Double.compare(price, 0) <= 0)
            return true;
        ElasticEconomy elasticEconomy = BlobLibAPI.getElasticEconomy();
        IdentityEconomy economy = elasticEconomy
                .map(Optional.ofNullable(getPriceCurrency()));
        boolean hasAmount = economy.has(player.getUniqueId(), price);
        if (!hasAmount)
            return false;
        economy.withdraw(player.getUniqueId(), price);
        return true;
    }

    /**
     * Will handle permission and payment of the button.
     * If both permission and payment is handled successfully, it will handle the action.
     *
     * @param player The player to handle the permission and payment for.
     * @return Whether the permission and payment was handled successfully.
     */
    public boolean handleAll(Player player) {
        boolean proceed = handlePermission(player) && handlePayment(player);
        if (!proceed)
            return false;
        handleAction(player);
        return true;
    }

    /**
     * @return The action of the button. Null if there is no action.
     */
    @Nullable
    public String getAction() {
        return action;
    }

    /**
     * Will handle the action of the button.
     *
     * @param entity The entity to handle the action for.
     */
    public void handleAction(Entity entity) {
        if (action == null)
            return;
        Action<Entity> fetch = BlobLibAssetAPI.getAction(action);
        fetch.perform(entity);
    }
}
