package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Uber;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class PlayerTask {
    private final String name;
    private final Uber<Integer> pending;
    private final BukkitTask spectatorTask;
    private boolean done;

    /**
     * Runs a synchronous PlayerTask
     *
     * @param player   The player to run the task on
     * @param plugin   The plugin to run the task on
     * @param consumer The consumer to run. Expect that player is online and not null.
     * @param seconds  The amount of seconds to run the task for
     */
    public static PlayerTask SYNCHRONOUS(Player player, JavaPlugin plugin, int seconds, Consumer<Player> consumer) {
        return new PlayerTask(player, plugin, consumer, seconds, false);
    }

    /**
     * Runs an asynchronous PlayerTask
     *
     * @param player   The player to run the task on
     * @param plugin   The plugin to run the task on
     * @param consumer The consumer to run. Expect that player is online and not null.
     * @param seconds  The amount of seconds to run the task for
     */
    public static PlayerTask ASYNCHRONOUS(Player player, JavaPlugin plugin, int seconds, Consumer<Player> consumer) {
        return new PlayerTask(player, plugin, consumer, seconds, true);
    }

    /**
     * Runs a PlayerTask
     *
     * @param player   The player to run the task on
     * @param plugin   The plugin to run the task on
     * @param consumer The consumer to run. Expect that player is online and not null.
     * @param seconds  The amount of seconds to run the task for
     * @param async    Whether to run the task asynchronously
     */
    public PlayerTask(Player player, JavaPlugin plugin,
                      Consumer<Player> consumer,
                      int seconds,
                      boolean async) {
        this.name = player.getName();
        this.done = false;
        this.pending = Uber.drive(seconds);
        this.spectatorTask = async ? new BukkitRunnable() {
            @Override
            public void run() {
                int current = pending.thanks();
                if (current > 0) {
                    current--;
                    pending.talk(current);
                    return;
                }
                Player player = getPlayer();
                cancel();
                if (player == null) return;
                consumer.accept(player);
                done = true;
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 20L)
                :
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int current = pending.thanks();
                        if (current > 0) {
                            current--;
                            pending.talk(current);
                            return;
                        }
                        Player player = getPlayer();
                        cancel();
                        if (player == null) return;
                        consumer.accept(player);
                        done = true;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
    }

    public String getName() {
        return name;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(name);
    }

    public boolean isDone() {
        return done;
    }

    public void cancel() {
        spectatorTask.cancel();
        done = true;
    }

    public int getPendingSeconds() {
        return pending.thanks();
    }

    public void setPendingSeconds(int seconds) {
        pending.talk(seconds);
    }

    public BukkitTask getTask() {
        return spectatorTask;
    }
}
