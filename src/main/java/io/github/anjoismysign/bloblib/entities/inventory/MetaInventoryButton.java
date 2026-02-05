package io.github.anjoismysign.bloblib.entities.inventory;

import io.github.anjoismysign.bloblib.action.ActionMemo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MetaInventoryButton extends InventoryButton {
    @NotNull
    private final String meta;
    @Nullable
    private final String subMeta;

    public MetaInventoryButton(@NotNull String key,
                               @NotNull Set<Integer> slots,
                               @NotNull String meta,
                               @Nullable String subMeta,
                               @Nullable String hasPermission,
                               double hasMoney,
                               @Nullable String priceCurrency,
                               @NotNull List<ActionMemo> actions,
                               @Nullable String hasTranslatableItem,
                               boolean isPermissionInverted,
                               boolean isMoneyInverted,
                               boolean isTranslatableItemInverted,
                               boolean cancelInteraction) {
        super(key, slots, hasPermission, hasMoney, priceCurrency, actions,
                hasTranslatableItem, isPermissionInverted, isMoneyInverted, isTranslatableItemInverted, cancelInteraction);
        this.meta = meta;
        this.subMeta = subMeta;
    }

    /**
     * @return Whether the MetaInventoryButton has meta or not
     */
    public boolean hasMeta() {
        return !getMeta().equals("NONE");
    }

    /**
     * @return The meta of the MetaInventoryButton
     */
    @NotNull
    public String getMeta() {
        return meta;
    }

    /**
     * @return The subMeta of the MetaInventoryButton if it has one
     * null otherwise
     */
    @Nullable
    public String getSubMeta() {
        return subMeta;
    }

    public void accept(ButtonVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Will clone/copy the button to a new instance
     *
     * @return The new instance of the button.
     */
    @Override
    public MetaInventoryButton copy() {
        return new MetaInventoryButton(getKey(), new HashSet<>(getSlots()), getMeta(), getSubMeta(),
                getHasPermission(), getHasMoney(), getPriceCurrency(), getActions(),
                getHasTranslatableItem(), isPermissionInverted(),
                isMoneyInverted(), isTranslatableItemInverted(),
                isCancelInteraction());
    }
}
