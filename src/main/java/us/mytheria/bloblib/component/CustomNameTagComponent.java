package us.mytheria.bloblib.component;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.component.textbubble.TextBubbleComponent;
import us.mytheria.bloblib.entities.MutableAddress;

public interface CustomNameTagComponent {
    MutableAddress<String> isListening = MutableAddress.of(null);

    @Nullable
    static String listening() {
        @Nullable String isListening = CustomNameTagComponent.isListening.look();
        return isListening;
    }

    static boolean isRegistered() {
        return listening() != null;
    }

    static void register(@NotNull Plugin plugin) {
        String isListening = listening();
        if (isListening != null) {
            plugin.getLogger().info(isListening + " already enabled CustomNameTagComponent. You might ignore this message");
            return;
        }
        @Nullable String noNameTagListening = NoNameTagComponent.isListening.look();
        @Nullable String textBubbleListening = TextBubbleComponent.isListening.look();
        if (noNameTagListening != null && textBubbleListening != null) {
            plugin.getLogger().info("CustomNameTagComponent was already and particularly enabled by some other plugin");
        }
        CustomNameTagComponent.isListening.set(plugin.getName());
        if (noNameTagListening == null)
            NoNameTagComponent.register(plugin);
        if (textBubbleListening == null)
            TextBubbleComponent.register(plugin);
    }
}
