package io.github.anjoismysign.bloblib.itemstack;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public record OmniStack(
        @NotNull Supplier<ItemStack> stackSupplier,
        @NotNull Consumer<ItemStackBuilder> builderConsumer
        ) {

    @NotNull
    public ItemStack getCopy(){
        ItemStack itemStack = stackSupplier.get();
        ItemStackBuilder builder = ItemStackBuilder.build(itemStack);
        builderConsumer.accept(builder);
        return builder.build();
    }
}
