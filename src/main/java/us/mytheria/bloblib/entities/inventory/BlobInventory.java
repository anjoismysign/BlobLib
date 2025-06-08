package us.mytheria.bloblib.entities.inventory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.api.BlobLibInventoryAPI;

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
