package io.github.anjoismysign.bloblib.entities.loot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public enum LootFunctionType {
    SET_COUNT("minecraft:set_count");

    private final String key;

    LootFunctionType(@NotNull String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @Nullable
    public static LootFunctionType byKey(@NotNull String key) {
        Objects.requireNonNull(key);
        for (LootFunctionType type : values()) {
            if (type.key.equals(key))
                return type;
        }
        return null;
    }
}
