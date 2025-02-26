package us.mytheria.bloblib.component;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;import us.mytheria.bloblib.component.textbubble.TextBubbleComponent;import us.mytheria.bloblib.component.textbubble.TextBubble1_19_4;import us.mytheria.bloblib.utilities.MinecraftVersion;

public enum ComponentFactory {
    INSTANCE;

    @Nullable
    public TextBubbleComponent textBubbleOf(@Nullable Entity belongsTo){
        if (belongsTo == null)
            return null;
        if (MinecraftVersion.getRunning().compareTo(MinecraftVersion.of("19.4")) > 0)
            return TextBubble1_19_4.of(belongsTo);
        return null;
    }
}
