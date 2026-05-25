package io.github.anjoismysign.bloblib.entities.loot;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record LootFunction(@NotNull LootFunctionType type,
                           @NotNull Map<String, Object> data) {
}
