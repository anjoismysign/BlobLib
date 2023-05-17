package us.mytheria.bloblib.entities.display;

import org.bukkit.entity.Display;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;

import java.util.function.Function;

public class DisplayDecorator<T extends Display> {
    private final T decorated;
    private BukkitTask perpetualTask;

    /**
     * Will create a new itemStack decorator.
     *
     * @param display the itemStack to wrap.
     */
    public DisplayDecorator(T display) {
        this.decorated = display;
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
        BukkitTask perpetualTask = new BukkitRunnable() {
            @Override
            public void run() {
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
        BukkitTask perpetualTask = new BukkitRunnable() {
            @Override
            public void run() {
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
        setPerpetualAsync(plugin, 0, interpolationDuration, () -> {
            transformRight(x, y, z, degrees, interpolationDuration);
        });
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
        setPerpetualAsync(plugin, 0, interpolationDuration, () -> {
            transformLeft(x, y, z, degrees, interpolationDuration,
                    interpolationDelay);
        });
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
        setPerpetualAsync(plugin, 0, interpolationDuration, () -> {
            transformRight(x, y, z, degrees, interpolationDuration);
        });
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
        setPerpetualAsync(plugin, 0, interpolationDuration, () -> {
            transformRight(x, y, z, degrees, interpolationDuration,
                    interpolationDelay);
        });
    }

    /**
     * Will open the TransformationBuilder passing the current transformation
     *
     * @return the TransformationBuilder
     */
    public TransformationBuilder transform() {
        return new TransformationBuilder(call().getTransformation());
    }

    /**
     * Will set a perpetual transformation that was
     * provided using asynchronous tasks.
     *
     * @param transformation the transformation.
     * @param plugin         the plugin used to schedule the task.
     */
    public void setPerpetualTransformation(Transformation transformation, JavaPlugin plugin) {
        setPerpetualAsync(plugin, 0, call().getInterpolationDuration(), () -> {
            decorated.setTransformation(transformation);
        });
    }

    /**
     * Will set a perpetual transformation,
     * passing DisplayDecorator#transform
     * and it needs to return TransformationBuilder#build
     *
     * @param javaPlugin the plugin used to schedule the task.
     * @param function   the function that will return the transformation.
     */
    public void transformPerpetually(JavaPlugin javaPlugin,
                                     Function<TransformationBuilder, Transformation> function) {
        Transformation transformation = function.apply(transform());
        setPerpetualTransformation(transformation, javaPlugin);
    }
}
