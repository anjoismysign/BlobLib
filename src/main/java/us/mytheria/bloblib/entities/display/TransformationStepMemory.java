package us.mytheria.bloblib.entities.display;

import org.bukkit.Bukkit;
import org.bukkit.entity.Display;
import org.bukkit.plugin.Plugin;

import java.util.ArrayDeque;
import java.util.List;

/**
 * This class is used to store a list of
 * transformation steps and play them
 * ordered. Cycle will be repeated
 * until stopped.
 *
 * @param <T> the type of the display
 */
public class TransformationStepMemory<T extends Display> {
    private final Plugin plugin;
    protected final ArrayDeque<TransformationStep> deque;
    private boolean isRunning;
    private boolean isLocked;

    /**
     * Will create a new TransformationStepMemory,
     * automatically adding the given list of
     * transformation steps to the memory.
     * The clock needs to be started manually
     *
     * @param list   the list of transformation steps
     * @param plugin the plugin to run the clock on
     * @param <T>    the type of the display
     * @return a new instance of TransformationStepMemory
     */
    public static <T extends Display> TransformationStepMemory<T> of(
            List<TransformationStep> list,
            Plugin plugin) {
        TransformationStepMemory<T> memory = new TransformationStepMemory<>(plugin);
        list.forEach(memory::addLast);
        return memory;
    }

    /**
     * Will create a new TransformationStepMemory
     *
     * @param plugin the plugin to run the clock on
     */
    public TransformationStepMemory(Plugin plugin) {
        this.isLocked = false;
        this.deque = new ArrayDeque<>();
        this.isRunning = false;
        this.plugin = plugin;
    }

    /**
     * Will add a transformation step to the memory
     * as last so the clock will later play it
     *
     * @param step the step to add
     */
    public void addLast(TransformationStep step) {
        deque.addLast(step);
    }

    /**
     * Will add a list of transformation steps
     * to the memory as last so the clock will later
     * play them
     *
     * @param steps the steps to add
     */
    public void addLast(List<TransformationStep> steps) {
        steps.forEach(this::addLast);
    }

    /**
     * Will play the next transformation step
     * and return it
     *
     * @param display the display to animate
     * @return the next transformation step
     */
    public TransformationStep playNext(T display) {
        if (!deque.isEmpty()) {
            TransformationStep current = deque.removeFirst();
            deque.addLast(current.copy());
            display.setInterpolationDelay(-1);
            display.setInterpolationDuration(current.getDuration());
            display.setTransformation(current.getTransformation());
            return current;
        } else
            throw new IllegalStateException("Memory is empty");
    }

    /**
     * Will start the transformation/animation clock.
     * It runs completely asynchronously.
     *
     * @param display the display to animate
     */
    public void startClock(T display) {
        if (isRunning)
            return;
        isRunning = true;
        clock(display);
    }

    /**
     * Checks if the clock is running
     *
     * @return true if the clock is running
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Checks if the clock is locked.
     * Once locked, the clock can't be started again.
     * Take it as a "final" state, similar to
     * marking an entity for removal,
     * as Entity#isValid() will return false
     *
     * @return true if the clock is locked
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Will stop the transformation/animation clock
     *
     * @param step    the step to stop at
     * @param display the display to animate
     */
    public void stopClock(TransformationStep step, T display) {
        if (!isRunning)
            return;
        isRunning = false;
        isLocked = true;
        display.setInterpolationDelay(-1);
        display.setInterpolationDuration(step.getDuration());
        display.setTransformation(step.getTransformation());
    }

    /**
     * Will stop the transformation/animation clock
     * using the next transformation step in the memory
     *
     * @param display the display to animate
     */
    public void stopClock(T display) {
        TransformationStep current = deque.peekFirst();
        if (current == null)
            return;
        stopClock(current, display);
    }

    private void clock(T display) {
        if (!isRunning)
            return;
        if (!display.isValid())
            return;
        int duration = playNext(display).getDuration();
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () ->
                clock(display), duration);
    }
}