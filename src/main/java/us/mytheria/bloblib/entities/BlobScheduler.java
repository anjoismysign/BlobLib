package us.mytheria.bloblib.entities;

import me.anjoismysign.winona.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public record BlobScheduler(@NotNull Plugin plugin) implements Scheduler<BukkitTask> {
    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    @Override
    public BukkitTask sync(@NotNull Runnable runnable) {
        return SCHEDULER.runTask(plugin, runnable);
    }

    @Override
    public BukkitTask syncLater(@NotNull Runnable runnable, long delay) {
        return SCHEDULER.runTaskLater(plugin, runnable, delay);
    }

    @Override
    public BukkitTask syncTimer(@NotNull Runnable runnable, long delay, long period) {
        return SCHEDULER.runTaskTimer(plugin, runnable, delay, period);
    }

    @Override
    public BukkitTask async(@NotNull Runnable runnable) {
        return SCHEDULER.runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public BukkitTask asyncLater(@NotNull Runnable runnable, long delay) {
        return SCHEDULER.runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    @Override
    public BukkitTask asyncTimer(@NotNull Runnable runnable, long delay, long period) {
        return SCHEDULER.runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }
}
