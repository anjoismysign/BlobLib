package io.github.anjoismysign.bloblib.entities.loot;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record LootPool(int rolls,
                       @NotNull List<LootEntry> entries) {
}
