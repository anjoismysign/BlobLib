package io.github.anjoismysign.bloblib.component;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface ComponentConsumer extends Plugin {

    default void registerComponent(@NotNull ComponentType componentType) {
        componentType.register(this);
    }
}
