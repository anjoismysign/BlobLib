package us.mytheria.bloblib.entities.inventory;

import net.kyori.adventure.translation.Translator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.api.BlobLibInventoryAPI;
import us.mytheria.bloblib.entities.PlayerAddress;
import us.mytheria.bloblib.entities.PlayerAddressBuilder;

import java.util.Objects;

public class BlobInventory extends SharableInventory<InventoryButton> {

    public static BlobInventory fromInventoryBuilderCarrier(InventoryBuilderCarrier<InventoryButton> carrier) {
        String title = Objects.requireNonNull(carrier.title(), "title cannot be null");
        return new BlobInventory(
                title,
                carrier.size(),
                carrier.buttonManager().copy(),
                carrier.reference(),
                carrier.locale());
    }

    @NotNull
    public static BlobInventory fromBlobInventoryOrFail(@NotNull String key,
                                                        @Nullable String locale){
        locale = locale == null ? "en_us" : locale;
        InventoryBuilderCarrier<InventoryButton> carrier = BlobLibInventoryAPI.getInstance()
                .getInventoryBuilderCarrier(key, locale);
        Objects.requireNonNull(carrier, "'"+key+"' doesn't point to a BlobInventory");
        return fromInventoryBuilderCarrier(carrier);
    }

    @NotNull
    public static BlobInventory fromBlobInventoryOrFail(@NotNull String key,
                                                        @NotNull PlayerAddress address){
        Objects.requireNonNull(address, "'address' cannot be null");
        @Nullable Player player = address.look();
        Objects.requireNonNull(player, "'address' must point to a valid Player");
        return fromBlobInventoryOrFail(key, player.getLocale());
    }

    public BlobInventory(@NotNull String title,
                         int size,
                         @NotNull ButtonManager<InventoryButton> buttonManager,
                         @Nullable String reference,
                         @Nullable String locale) {
        super(title, size, buttonManager, reference, locale);
    }

    public BlobInventory(@NotNull String title,
                         int size,
                         @NotNull ButtonManager<InventoryButton> buttonManager) {
        super(title, size, buttonManager);
    }

    @Override
    @NotNull
    public BlobInventory copy() {
        return new BlobInventory(
                getTitle(),
                getSize(),
                getButtonManager().copy(),
                getReference(),
                getLocale());
    }
}
