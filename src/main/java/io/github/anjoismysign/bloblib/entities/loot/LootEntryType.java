package io.github.anjoismysign.bloblib.entities.loot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public enum LootEntryType {
    ITEM("minecraft:item"),
    EMPTY("minecraft:empty"),
    TRANSLATABLE_ITEM("bloblib:translatableitem");

    private final String key;

    LootEntryType(@NotNull String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @Nullable
    public static LootEntryType byKey(@NotNull String key) {
        Objects.requireNonNull(key);
        for (LootEntryType type : values()) {
            if (type.key.equals(key))
                return type;
        }
        return null;
    }
}
