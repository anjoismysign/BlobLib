package io.github.anjoismysign.bloblib.component;

import io.github.anjoismysign.bloblib.component.textbubble.TextBubble1_19_4;
import io.github.anjoismysign.bloblib.component.textbubble.TextBubbleComponent;
import io.github.anjoismysign.bloblib.utilities.MinecraftVersion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public enum ComponentFactory {
    INSTANCE;

    @Nullable
    public TextBubbleComponent textBubbleOf(@Nullable Player belongsTo){
        if (belongsTo == null)
            return null;
        if (MinecraftVersion.getRunning().compareTo(MinecraftVersion.of("19.4")) > 0)
            return TextBubble1_19_4.of(belongsTo);
        return null;
    }
}
