package io.github.anjoismysign.bloblib.component;

import io.github.anjoismysign.bloblib.component.textbubble.TextBubbleComponent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public enum ComponentType {
    TEXT_BUBBLE(TextBubbleComponent::register),
    NO_NAME_TAG(NoNameTagComponent::register),
    CUSTOM_NAME_TAG(CustomNameTagComponent::register);

    private final Consumer<Plugin> registerConsumer;

    ComponentType(@NotNull Consumer<Plugin> registerConsumer) {
        this.registerConsumer = registerConsumer;
    }

    public void register(@NotNull Plugin plugin) {
        registerConsumer.accept(plugin);
    }
}
