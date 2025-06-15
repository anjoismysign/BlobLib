package io.github.anjoismysign.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * A plugin operator is made to be able to make operations that
 * require a Plugin instance.
 * It's ideal to make an Asset using an Interface that contains
 * this asset's operations and then implement this interface
 * in the Asset class.
 * Through this way, only the Asset class would be exposed to
 * the API user.
 */
public interface BukkitPluginOperator {
    static BukkitPluginOperator of(Plugin plugin) {
        return () -> plugin;
    }

    /**
     * Gets the plugin that manages this PluginOperator.
     *
     * @return the plugin that manages this PluginOperator.
     */
    Plugin getPlugin();

    /**
     * Will run a task in the main thread.
     *
     * @param runnable the task to run
     * @return the BukkitTask that is running the task
     */
    default BukkitTask runTask(Runnable runnable) {
        return Bukkit.getScheduler().runTask(getPlugin(), runnable);
    }

    /**
     * Will run a task in the main thread after the given delay.
     *
     * @param runnable the task to run
     * @param delay    the delay
     * @param unit     the unit of the delay
     * @return the BukkitTask that is running the task
     */
    default BukkitTask runTaskLater(Runnable runnable, long delay, MinecraftTimeUnit unit) {
        if (unit == MinecraftTimeUnit.TICKS)
            return Bukkit.getScheduler().runTaskLater(getPlugin(), runnable, delay);
        long x = (long) MinecraftTimeUnit.TICKS.convert(delay, unit);
        return Bukkit.getScheduler().runTaskLater(getPlugin(), runnable, x);
    }

    /**
     * Will run a task in the main thread after the given delay.
     *
     * @param runnable the task to run
     * @param delay    the delay in ticks
     * @return the BukkitTask that is running the task
     */
    default BukkitTask runTaskLater(Runnable runnable, long delay) {
        return runTaskLater(runnable, delay, MinecraftTimeUnit.TICKS);
    }

    /**
     * Will run a task in the main thread after the given delay and repeat it every period.
     *
     * @param runnable the task to run
     * @param delay    the delay
     * @param period   the period
     * @param unit     the unit of the delay and period
     * @return the BukkitTask that is running the task
     */
    default BukkitTask runTaskTimer(Runnable runnable, long delay, long period, MinecraftTimeUnit unit) {
        if (unit == MinecraftTimeUnit.TICKS)
            return Bukkit.getScheduler().runTaskTimer(getPlugin(), runnable, delay, period);
        long x = (long) MinecraftTimeUnit.TICKS.convert(delay, unit);
        long y = (long) MinecraftTimeUnit.TICKS.convert(period, unit);
        return Bukkit.getScheduler().runTaskTimer(getPlugin(), runnable, x, y);
    }

    /**
     * Will run a task in the main thread after the given delay and repeat it every period.
     *
     * @param runnable the task to run
     * @param delay    the delay in ticks
     * @param period   the period in ticks
     * @return the BukkitTask that is running the task
     */
    default BukkitTask runTaskTimer(Runnable runnable, long delay, long period) {
        return runTaskTimer(runnable, delay, period, MinecraftTimeUnit.TICKS);
    }

    /**
     * Will run a task in the async thread.
     *
     * @param runnable the task to run
     * @return the BukkitTask that is running the task
     */
    default BukkitTask runTaskAsynchronously(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), runnable);
    }

    /**
     * Will run a task in the async thread after the given delay.
     *
     * @param runnable the task to run
     * @param delay    the delay
     * @param unit     the unit of the delay
     * @return the BukkitTask that is running the task
     */
    default BukkitTask runTaskLaterAsynchronously(Runnable runnable, long delay, MinecraftTimeUnit unit) {
        if (unit == MinecraftTimeUnit.TICKS)
            return Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), runnable, delay);
        long x = (long) MinecraftTimeUnit.TICKS.convert(delay, unit);
        return Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), runnable, x);
    }

    /**
     * Will run a task in the async thread after the given delay.
     *
     * @param runnable the task to run
     * @param delay    the delay in ticks
     * @return the BukkitTask that is running the task
     */
    default BukkitTask runTaskLaterAsynchronously(Runnable runnable, long delay) {
        return runTaskLaterAsynchronously(runnable, delay, MinecraftTimeUnit.TICKS);
    }

    /**
     * Will run a task in the async thread after the given delay and repeat it every period.
     *
     * @param runnable the task to run
     * @param delay    the delay
     * @param period   the period
     * @param unit     the unit of the delay and period
     * @return the BukkitTask that is running the task
     */
    default BukkitTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period, MinecraftTimeUnit unit) {
        if (unit == MinecraftTimeUnit.TICKS)
            return Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), runnable, delay, period);
        long x = (long) MinecraftTimeUnit.TICKS.convert(delay, unit);
        long y = (long) MinecraftTimeUnit.TICKS.convert(period, unit);
        return Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), runnable, x, y);
    }

    /**
     * Will run a task in the async thread after the given delay and repeat it every period.
     *
     * @param runnable the task to run
     * @param delay    the delay in ticks
     * @param period   the period in ticks
     * @return the BukkitTask that is running the task
     */
    default BukkitTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period) {
        return runTaskTimerAsynchronously(runnable, delay, period, MinecraftTimeUnit.TICKS);
    }

    @NotNull
    default NamespacedKey generateNamespacedKey(String key) {
        return new NamespacedKey(getPlugin(), key);
    }
}
