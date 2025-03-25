package us.mytheria.bloblib.component.textbubble;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.world.WorldLoadEvent;
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

    @Nullable
    static String listening() {
        @Nullable String isListening = TextBubbleComponent.isListening.look();
        return isListening;
    }

    static boolean isRegistered() {
        return listening() != null;
    }

    static void register(@NotNull Plugin plugin) {
        Logger logger = plugin.getLogger();
        String isListening = listening();
        if (isListening != null) {
            logger.info(isListening + " already enabled TextBubbleComponent. You might ignore this message");
            return;
        }
        TextBubbleComponent.isListening.set(plugin.getName());
        Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true));
        Bukkit.getPluginManager().registerEvents(
                new Listener() {

                    @EventHandler
                    public void onQuit(PlayerQuitEvent event) {
                        Player player = event.getPlayer();
                        UUID id = player.getUniqueId();
                        playerOwnerTracking.remove(id);
                    }

                    @EventHandler
                    public void world(WorldLoadEvent event) {
                        World world = event.getWorld();
                        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
                    }

                    @EventHandler
                    public void death(PlayerDeathEvent event) {
                        UUID id = event.getEntity().getUniqueId();
                        @Nullable TextBubbleComponent bubble = playerOwnerTracking.get(id);
                        if (bubble == null)
                            return;
                        bubble.remove();
                    }

                    @EventHandler(priority = EventPriority.NORMAL)
                    public void spawn(PlayerPostRespawnEvent event) {
                        Player player = event.getPlayer();
                        GameMode gameMode = player.getGameMode();
                        if (gameMode.isInvulnerable())
                            return;
                        UUID id = player.getUniqueId();
                        @Nullable TextBubbleComponent bubble = playerOwnerTracking.get(id);
                        if (bubble == null)
                            return;
                        bubble.spawn();
                    }

                    @EventHandler(priority = EventPriority.NORMAL)
                    public void onGameMode(PlayerGameModeChangeEvent event) {
                        Player player = event.getPlayer();
                        UUID id = player.getUniqueId();
                        @Nullable TextBubbleComponent bubble = playerOwnerTracking.get(id);
                        if (bubble == null)
                            return;
                        GameMode gameMode = event.getNewGameMode();
                        if (gameMode.isInvulnerable()) {
                            bubble.remove();
                            return;
                        }
                        Bukkit.getScheduler().runTask(plugin, bubble::spawn);
                    }

                    @EventHandler(ignoreCancelled = true)
                    public void sneak(PlayerToggleSneakEvent event) {
                        Player player = event.getPlayer();
                        UUID id = player.getUniqueId();
                        @Nullable TextBubbleComponent bubble = playerOwnerTracking.get(id);
                        if (bubble == null)
                            return;
                        GameMode gameMode = player.getGameMode();
                        if (gameMode.isInvulnerable())
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

    void remove();

    void spawn();

    @NotNull
    Player belongsTo();

    @Nullable
    String getText();

    void setText(@Nullable String text);

    default boolean isValid() {
        return playerOwnerTracking.containsValue(this);
    }
}
