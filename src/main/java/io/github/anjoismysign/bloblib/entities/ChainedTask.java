package io.github.anjoismysign.bloblib.entities;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ChainedTask {
    private final @NotNull CompletableFuture<Void> future;
    private BukkitTask task;
    private final long estimatedTime;
    private final long eta;
    private @Nullable Consumer<Integer> progressConsumer;

    public ChainedTask(@NotNull CompletableFuture<Void> future,
                       @Nullable BukkitTask task,
                       long estimatedTime) {
        this.future = future;
        this.task = task;
        this.estimatedTime = estimatedTime;
        this.eta = Instant.now().toEpochMilli() + (estimatedTime * 50);
        this.progressConsumer = null;
    }

    public @NotNull CompletableFuture<Void> getFuture() {
        return future;
    }

    public @NotNull BukkitTask getTask() {
        return task;
    }

    public void setTask(@NotNull BukkitTask task) {
        this.task = Objects.requireNonNull(task);
    }

    /**
     * Get the estimated time in minecraft ticks that this task will take to complete.
     *
     * @return the estimated time in minecraft ticks that this task will take to complete.
     */
    public long getEstimatedTime() {
        return estimatedTime;
    }

    /**
     * Get the estimated time arrival in milliseconds that this task will take to complete.
     * This is using Instant.now().toEpochMilli() + (estimatedTime * 50)
     * Might want to use @{@link Instant#ofEpochMilli(long)}
     *
     * @return the estimated time in milliseconds that this task will take to complete.
     */
    public long getETA() {
        return eta;
    }

    /**
     * Sets the progress consumer for this task.
     *
     * @param progressConsumer the progress consumer for this task.
     */
    public void setProgressConsumer(@Nullable Consumer<Integer> progressConsumer) {
        this.progressConsumer = progressConsumer;
    }

    public void accept(Integer integer) {
        if (progressConsumer != null)
            progressConsumer.accept(integer);
    }

    /**
     * Should cancel the current task and complete the future exceptionally.
     */
    public void forceCancel() {
        task.cancel();
        future.completeExceptionally(new RuntimeException("Task was cancelled"));
    }
}
