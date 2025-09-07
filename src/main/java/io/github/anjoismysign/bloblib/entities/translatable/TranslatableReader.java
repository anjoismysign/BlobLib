package io.github.anjoismysign.bloblib.entities.translatable;

import io.github.anjoismysign.bloblib.FluidPressureAPI;
import io.github.anjoismysign.bloblib.ProjectileDamageAPI;
import io.github.anjoismysign.bloblib.SoulAPI;
import io.github.anjoismysign.bloblib.UniqueAPI;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import io.github.anjoismysign.bloblib.middleman.itemstack.ItemStackReader;
import io.github.anjoismysign.bloblib.middleman.itemstack.OmniStack;
import io.github.anjoismysign.bloblib.utilities.TextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class TranslatableReader {

    public static TranslatableItem ITEM(@NotNull ConfigurationSection section,
                                        @NotNull String locale,
                                        @NotNull String key) {
        Objects.requireNonNull(section, "Section cannot be null");
        Objects.requireNonNull(locale, "Locale cannot be null");
        Objects.requireNonNull(key, "Key cannot be null");
        if (!section.isConfigurationSection("ItemStack"))
            throw new ConfigurationFieldException("'ItemStack' is missing or not set");
        boolean isSoul = section.getBoolean("Is-Soul", false);
        boolean isUnique = section.getBoolean("Is-Unique", false);
        OmniStack omniStack = ItemStackReader.OMNI_STACK(section.getConfigurationSection("ItemStack"));
        final Supplier<ItemStack> supplier = () -> {
            ItemStack itemStack = omniStack.getCopy();
            @Nullable ItemMeta itemMeta = itemStack.getItemMeta();
            Objects.requireNonNull(itemMeta, "'itemMeta' cannot be null");
            CustomModelDataComponent dataComponent = itemMeta.getCustomModelDataComponent();
            List<String> list = new ArrayList<>(dataComponent.getStrings());
            list.add(TranslatableItem.KEY_PREFIX + key);
            list.add(TranslatableItem.LOCALE_PREFIX + locale);
            dataComponent.setStrings(list);
            itemMeta.setCustomModelDataComponent(dataComponent);
            itemStack.setItemMeta(itemMeta);
            if (isSoul)
                SoulAPI.getInstance().set(itemStack);
            if (isUnique)
                UniqueAPI.getInstance().set(itemStack);
            if (section.isDouble("Fluid-Pressure")) {
                double pressure = section.getDouble("Fluid-Pressure");
                FluidPressureAPI.getInstance().set(itemStack, pressure);
            }
            if (section.isDouble("Projectile-Damage")) {
                double damage = section.getDouble("Projectile-Damage");
                ProjectileDamageAPI.getInstance().set(itemStack, damage);
            }
            return itemStack;
        };
        return BlobTranslatableItem.of(key, locale, supplier);
    }

    public static TranslatableSnippet SNIPPET(@NotNull ConfigurationSection section,
                                              @NotNull String locale,
                                              @NotNull String key) {
        Objects.requireNonNull(section, "Section cannot be null");
        Objects.requireNonNull(locale, "Locale cannot be null");
        Objects.requireNonNull(key, "Key cannot be null");
        if (!section.isString("Snippet"))
            throw new ConfigurationFieldException("'Snippet' field is required for TranslatableSnippets at " + section.getCurrentPath());
        String snippet = Objects.requireNonNull(section.getString("Snippet"));
        snippet = TextColor.PARSE(snippet);
        return BlobTranslatableSnippet.of(key, locale, snippet);
    }

    public static TranslatableBlock BLOCK(@NotNull ConfigurationSection section,
                                          @NotNull String locale,
                                          @NotNull String key) {
        Objects.requireNonNull(section, "Section cannot be null");
        Objects.requireNonNull(locale, "Locale cannot be null");
        Objects.requireNonNull(key, "Key cannot be null");
        List<String> lines = section.getStringList("Block");
        if (lines.isEmpty())
            throw new ConfigurationFieldException("'Block' field is required for TranslatableBlocks");
        lines = lines.stream().map(TextColor::PARSE).toList();
        return BlobTranslatableBlock.of(key, locale, lines);
    }
}
