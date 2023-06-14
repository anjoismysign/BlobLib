package us.mytheria.bloblib.entities.display;

import org.bukkit.entity.Display;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;

import java.util.List;
import java.util.function.Function;

public class DisplayDecorator<T extends Display> {
    private final T decorated;
    private final JavaPlugin plugin;
    private TransformationStepMemory<T> stepMemory;
    private BukkitTask perpetualTask;

    /**
     * Will create a new itemStack decorator.
     *
     * @param display the itemStack to wrap.
     */
    public DisplayDecorator(T display, JavaPlugin plugin) {
        this.decorated = display;
        this.plugin = plugin;
        this.stepMemory = new TransformationStepMemory<>(plugin);
    }

    /**
     * Will return the wrapped itemStack.
     *
     * @return the wrapped itemStack.
     */
    public T call() {
        return decorated;
    }

    /**
     * Will return the last step in the memory.
     */
    public TransformationStep peekLastStep() {
        return stepMemory.deque.peekLast();
    }

    /**
     * Will add provided step as last step in the memory.
     * If clock had not started, it will start it.
     *
     * @param step the step to add.
     */
    public void learnStep(TransformationStep step) {
        stepMemory.addLast(step);
        stepMemory.startClock(call());
    }

    /**
     * Will add provided steps as last steps in the memory.
     * If clock had not started, it will start it.
     *
     * @param steps the steps to add.
     */
    public void learnSteps(List<TransformationStep> steps) {
        stepMemory.addLast(steps);
        stepMemory.startClock(call());
    }

    /**
     * Will stop the transformation/animation clock
     * using the next transformation step in the memory.
     * Will loc
     */
    public void stopClock() {
        stepMemory.stopClock(call());
        stepMemory = new TransformationStepMemory<>(plugin);
    }

    /**
     * Will stop the transformation/animation clock
     *
     * @param step the step to stop at
     */
    public void stopClock(TransformationStep step) {
        stepMemory.stopClock(step, call());
    }

    /**
     * Will transform the itemStack to leftRotation.
     * Will use '-1' as the interpolation delay.
     *
     * @param x                     the x-axis of the rotation.
     * @param y                     the y-axis of the rotation.
     * @param z                     the z-axis of the rotation.
     * @param degrees               the degrees of the rotation (in degrees, not radians).
     * @param interpolationDuration the duration of the interpolation.
     */
    public void transformLeft(float x, float y, float z, float degrees,
                              int interpolationDuration) {
        transformLeft(x, y, z, degrees, interpolationDuration, -1);
    }

    /**
     * Will transform the itemStack to leftRotation.
     *
     * @param x                     the x-axis of the rotation.
     * @param y                     the y-axis of the rotation.
     * @param z                     the z-axis of the rotation.
     * @param degrees               the degrees of the rotation (in degrees, not radians).
     * @param interpolationDuration the duration of the interpolation.
     * @param interpolationDelay    the delay of the interpolation.
     */
    public void transformLeft(float x, float y, float z, float degrees,
                              int interpolationDuration,
                              int interpolationDelay) {
        degrees = (float) Math.toRadians(degrees);
        decorated.setInterpolationDuration(interpolationDuration);
        decorated.setInterpolationDelay(interpolationDelay);
        Transformation transformation = decorated.getTransformation();
        transformation.getLeftRotation()
                .set(new AxisAngle4f(degrees, x, y, z));
        decorated.setTransformation(transformation);
    }

    /**
     * Will transform the itemStack to leftRotation.
     * Will use '-1' as the interpolation delay.
     *
     * @param x                     the x-axis of the rotation.
     * @param y                     the y-axis of the rotation.
     * @param z                     the z-axis of the rotation.
     * @param degrees               the degrees of the rotation (in degrees, not radians).
     * @param interpolationDuration the duration of the interpolation.
     */
    public void transformRight(float x, float y, float z, float degrees,
                               int interpolationDuration) {
        transformRight(x, y, z, degrees, interpolationDuration, -1);
    }

    /**
     * Will transform the itemStack to rightRotation.
     *
     * @param x                     the x-axis of the rotation.
     * @param y                     the y-axis of the rotation.
     * @param z                     the z-axis of the rotation.
     * @param degrees               the degrees of the rotation (in degrees, not radians).
     * @param interpolationDuration the duration of the interpolation.
     * @param interpolationDelay    the delay of the interpolation.
     */
    public void transformRight(float x, float y, float z, float degrees,
                               int interpolationDuration,
                               int interpolationDelay) {
        degrees = (float) Math.toRadians(degrees);
        decorated.setInterpolationDuration(interpolationDuration);
        decorated.setInterpolationDelay(interpolationDelay);
        Transformation transformation = decorated.getTransformation();
        transformation.getRightRotation()
                .set(new AxisAngle4f(degrees, x, y, z));
        decorated.setTransformation(transformation);
    }

    /**
     * Will set a perpetual task asynchronously.
     * If there is already a perpetual task, it will cancel it
     * before setting the new one.
     *
     * @param plugin   the plugin used to schedule the task.
     * @param delay    the delay.
     * @param period   the period.
     * @param runnable the runnable.
     */
    public void setPerpetualAsync(JavaPlugin plugin, long delay,
                                  long period, Runnable runnable) {
        if (perpetualTask != null) {
            perpetualTask.cancel();
        }
        perpetualTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!decorated.isValid()) {
                    cancel();
                    return;
                }
                runnable.run();
            }
        }.runTaskTimerAsynchronously(plugin, delay, period);
    }

    /**
     * Will set a perpetual task synchronously.
     * If there is already a perpetual task, it will cancel it
     * before setting the new one.
     *
     * @param plugin   the plugin used to schedule the task.
     * @param delay    the delay.
     * @param period   the period.
     * @param runnable the runnable.
     */
    public void setPerpetual(JavaPlugin plugin, long delay,
                             long period, Runnable runnable) {
        if (perpetualTask != null) {
            perpetualTask.cancel();
        }
        perpetualTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!decorated.isValid()) {
                    cancel();
                    return;
                }
                runnable.run();
            }
        }.runTaskTimer(plugin, delay, period);
    }

    /**
     * Will transform the itemStack to leftRotation
     * perpetually using asynchronous tasks.
     *
     * @param x                     the x-axis of the rotation.
     * @param y                     the y-axis of the rotation.
     * @param z                     the z-axis of the rotation.
     * @param degrees               the degrees of the rotation (in degrees, not radians).
     * @param interpolationDuration the duration of the interpolation.
     * @param plugin                the plugin used to schedule the task
     */
    public void transformLeftPerpetual(float x, float y, float z, float degrees,
                                       int interpolationDuration,
                                       JavaPlugin plugin) {
        setPerpetualAsync(plugin, -1, interpolationDuration, () -> transformRight(x, y, z, degrees, interpolationDuration));
    }

    /**
     * Will transform the itemStack to leftRotation
     * perpetually using asynchronous tasks.
     *
     * @param x                     the x-axis of the rotation.
     * @param y                     the y-axis of the rotation.
     * @param z                     the z-axis of the rotation.
     * @param degrees               the degrees of the rotation (in degrees, not radians).
     * @param interpolationDuration the duration of the interpolation.
     * @param interpolationDelay    the delay of the interpolation.
     * @param plugin                the plugin used to schedule the task
     */
    public void transformLeftPerpetual(float x, float y, float z, float degrees,
                                       int interpolationDuration,
                                       int interpolationDelay,
                                       JavaPlugin plugin) {
        setPerpetualAsync(plugin, -1, interpolationDuration, () -> transformLeft(x, y, z, degrees, interpolationDuration,
                interpolationDelay));
    }

    /**
     * Will transform the itemStack to rightRotation
     * perpetually using asynchronous tasks.
     *
     * @param x                     the x-axis of the rotation.
     * @param y                     the y-axis of the rotation.
     * @param z                     the z-axis of the rotation.
     * @param degrees               the degrees of the rotation (in degrees, not radians).
     * @param interpolationDuration the duration of the interpolation.
     * @param plugin                the plugin used to schedule the task
     */
    public void transformRightPerpetual(float x, float y, float z, float degrees,
                                        int interpolationDuration,
                                        JavaPlugin plugin) {
        setPerpetualAsync(plugin, -1, interpolationDuration, () -> transformRight(x, y, z, degrees, interpolationDuration));
    }

    /**
     * Will transform the itemStack to rightRotation
     * perpetually using asynchronous tasks.
     *
     * @param x                     the x-axis of the rotation.
     * @param y                     the y-axis of the rotation.
     * @param z                     the z-axis of the rotation.
     * @param degrees               the degrees of the rotation (in degrees, not radians).
     * @param interpolationDuration the duration of the interpolation.
     * @param interpolationDelay    the delay of the interpolation.
     * @param plugin                the plugin used to schedule the task
     */
    public void transformRightPerpetual(float x, float y, float z, float degrees,
                                        int interpolationDuration,
                                        int interpolationDelay,
                                        JavaPlugin plugin) {
        setPerpetualAsync(plugin, -1, interpolationDuration, () -> transformRight(x, y, z, degrees, interpolationDuration,
                interpolationDelay));
    }

    /**
     * Will open the TransformationStepFactory passing the current transformation
     *
     * @param interpolationDuration the duration of the interpolation (in ticks).
     * @return the TransformationBuilder
     */
    public TransformationStepFactory manufacture(int interpolationDuration) {
        T call = call();
        return new TransformationStepFactory(
                new TransformationStep(call.getTransformation(),
                        call.getShadowRadius(),
                        call.getShadowStrength(),
                        interpolationDuration));
    }

    /**
     * Will open the TransformationStepFactory passing the current transformation
     * Will use the default interpolation duration of 20 ticks (1 second).
     *
     * @return the TransformationBuilder
     */
    public TransformationStepFactory manufacture() {
        return manufacture(20);
    }

    /**
     * public TransformationStepFactory manufacture() {
     * return manufacture(20);
     * }
     * <p>
     * /**
     * Will set a perpetual transformation that was
     * provided using asynchronous tasks.
     *
     * @param function the function that will provide the transformation.
     * @param plugin   the plugin used to schedule the task.
     */
    public void setPerpetualTransformation(Function<TransformationStepFactory, Transformation> function, JavaPlugin plugin) {
        setPerpetualAsync(plugin, -1, call().getInterpolationDuration(), () -> decorated.setTransformation(function.apply(manufacture())));
    }
}
