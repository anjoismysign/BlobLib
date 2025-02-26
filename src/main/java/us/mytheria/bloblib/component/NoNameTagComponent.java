package us.mytheria.bloblib.component;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.MutableAddress;

import java.util.function.Consumer;

public interface NoNameTagComponent {
    MutableAddress<String> isListening = MutableAddress.of(null);

    static void register(@NotNull Plugin plugin) {
        @Nullable String isListening = NoNameTagComponent.isListening.look();
        if (isListening != null) {
            plugin.getLogger().info(isListening + " already enabled NoNameTagComponent. You might ignore this message");
            return;
        }
        NoNameTagComponent.isListening.set(plugin.getName());
        Consumer<ScoreboardManager> runnable = scoreboardManager -> {
            Scoreboard score = scoreboardManager.getMainScoreboard();
            Team team = score.getTeam("bloblib");
            if (team == null) {
                team = score.registerNewTeam("bloblib");
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            }

            Team finalTeam = team;
            Bukkit.getPluginManager().registerEvents(
                    new Listener() {
                        @EventHandler
                        public void join(PlayerJoinEvent event) {
                            finalTeam.addEntry(event.getPlayer().getName());
                        }
                    }
                    ,
                    plugin);
        };
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null)
            Bukkit.getScheduler().runTask(plugin, () -> {
                runnable.accept(Bukkit.getScoreboardManager());
            });
        else
            runnable.accept(scoreboardManager);
    }
}
