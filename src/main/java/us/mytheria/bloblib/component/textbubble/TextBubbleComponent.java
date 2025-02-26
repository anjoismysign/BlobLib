package us.mytheria.bloblib.component.textbubble;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.MutableAddress;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public interface TextBubbleComponent {
    MutableAddress<String> isListening = MutableAddress.of(null);

    static void register(@NotNull Plugin plugin) {
        Logger logger = plugin.getLogger();
        @Nullable String isListening = TextBubbleComponent.isListening.look();
        if (isListening != null) {
            logger.info(isListening + " already enabled NoNameTagComponent. You might ignore this message");
            return;
        }
        TextBubbleComponent.isListening.set(plugin.getName());
        Bukkit.getPluginManager().registerEvents(
                new Listener() {

                    @EventHandler
                    public void death(PlayerDeathEvent event) {
                        UUID id = event.getEntity().getUniqueId();
                        @Nullable TextBubbleComponent bubble = playerOwnerTracking.get(id);
                        if (bubble == null)
                            return;
                        bubble.remove();
                    }

                    @EventHandler
                    public void spawn(PlayerPostRespawnEvent event) {
                        UUID id = event.getPlayer().getUniqueId();
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            @Nullable TextBubbleComponent bubble = playerOwnerTracking.get(id);
                            if (bubble == null)
                                return;
                            bubble.spawn();
                        });
                    }

                    @EventHandler(ignoreCancelled = true)
                    public void sneak(PlayerToggleSneakEvent event) {
                        UUID id = event.getPlayer().getUniqueId();
                        @Nullable TextBubbleComponent bubble = playerOwnerTracking.get(id);
                        if (bubble == null)
                            return;
                        if (event.isSneaking())
                            bubble.remove();
                        else
                            bubble.spawn();
                    }
                },
                plugin
        );
    }

    Map<Entity, TextBubbleComponent> entityTracking = new HashMap<>();
    Map<UUID, TextBubbleComponent> playerOwnerTracking = new HashMap<>();
    Map<UUID, TextBubbleComponent> entityOwnerTracking = new HashMap<>();

    void remove();

    void spawn();

    @NotNull
    Entity belongsTo();

    @Nullable
    String getText();

    void setText(@Nullable String text);
}
