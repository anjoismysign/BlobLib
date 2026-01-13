package io.github.anjoismysign.bloblib.middleman.itemstack;

import io.github.anjoismysign.bloblib.events.OmniStackUpdateEvent;
import io.github.anjoismysign.bloblib.utilities.TextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record OmniStack(
        @NotNull Supplier<ItemStack> stackSupplier,
        @NotNull Consumer<ItemStackBuilder> builderConsumer,
        @Nullable String linkedItem,
        boolean isMiniMessage,
        @Nullable List<String> readLore,
        @Nullable String readDisplayName,
        @Nullable String readItemName
        ) {

    @NotNull
    public ItemStack getCopy(){
        ItemStack itemStack = stackSupplier.get();
        ItemStackBuilder builder = ItemStackBuilder.build(itemStack);
        builderConsumer.accept(builder);
        ItemStack copy = builder.build();
        update(copy, null);
        return copy;
    }

    @NotNull
    public ItemStack getCopy(@NotNull Player player){
        Objects.requireNonNull(player, "'player' cannot be null");
        ItemStack itemStack = stackSupplier.get();
        ItemStackBuilder builder = ItemStackBuilder.build(itemStack);
        builderConsumer.accept(builder);
        ItemStack copy = builder.build();
        update(copy, player);
        return copy;
    }

    private void update(@NotNull ItemStack stack,
                        @Nullable Player player){
        OmniStackUpdateEvent event = new OmniStackUpdateEvent(
                this,
                player,
                isMiniMessage,
                readLore,
                readDisplayName,
                readItemName
        );
        Bukkit.getPluginManager().callEvent(event);

        @Nullable List<String> readLore = event.getReadLore();
        @Nullable String readDisplayName = event.getReadDisplayName();
        @Nullable String readItemName = event.getReadItemName();

        ItemMeta meta = stack.getItemMeta();
        if (isMiniMessage) {
            MiniMessage miniMessage = MiniMessage.miniMessage();
            @Nullable List<Component> lore = readLore == null ? null : readLore.stream().map(miniMessage::deserialize).toList();
            @Nullable Component displayName = readDisplayName == null ? null : miniMessage.deserialize(readDisplayName);
            @Nullable Component itemName = readItemName == null ? null : miniMessage.deserialize(readItemName);

            meta.lore(lore);
            meta.displayName(displayName);
            meta.itemName(itemName);

        } else {
            if (readLore != null){
                List<String> lore = new ArrayList<>();
                readLore.forEach(string -> lore.add(TextColor.PARSE(string)));
                meta.setLore(lore);
            }
            if (readDisplayName != null) {
                meta.setDisplayName(TextColor.PARSE(readDisplayName));
            }
            if (readItemName != null) {
                meta.setItemName(TextColor.PARSE(readItemName));
            }
        }
        stack.setItemMeta(meta);
    }

}
