package us.mytheria.bloblib.entities;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Fuel {
    @Nullable
    private final ItemStack replacement;
    private final long burnTime;

    /**
     * Creates a new Fuel object with a replacement.
     *
     * @param replacement The item that will replace the fuel item.
     * @param burnTime    The burn time of the item.
     * @return The new Fuel object.
     */
    public static Fuel withReplacement(@NotNull ItemStack replacement, long burnTime) {
        return new Fuel(Objects.requireNonNull(replacement), burnTime);
    }

    /**
     * Creates a new Fuel object without a replacement.
     *
     * @param burnTime The burn time of the item.
     * @return The new Fuel object.
     */
    public static Fuel of(long burnTime) {
        return new Fuel(null, burnTime);
    }

    private Fuel(@Nullable ItemStack replacement, long burnTime) {
        this.replacement = replacement;
        this.burnTime = burnTime;
    }

    /**
     * Gets the replacement item.
     * If not null, this item should replace
     * the fuel item whenever done the smelt operation.
     *
     * @return The replacement item.
     */
    @Nullable
    public ItemStack getReplacement() {
        return replacement;
    }

    /**
     * Gets the burn time of the item.
     *
     * @return The burn time of the item.
     */
    public long getBurnTime() {
        return burnTime;
    }
}
