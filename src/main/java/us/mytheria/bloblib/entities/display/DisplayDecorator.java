package us.mytheria.bloblib.entities.display;

import org.bukkit.entity.Display;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;

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
     * @param degree                the degree of the rotation (in degrees, not radians).
     * @param interpolationDuration the duration of the interpolation.
     */
    public void transformLeft(float x, float y, float z, float degree,
                              int interpolationDuration) {
        transformLeft(x, y, z, degree, interpolationDuration, -1);

    }

    /**
     * Will transform the itemStack to leftRotation.
     *
     * @param x                     the x-axis of the rotation.
     * @param y                     the y-axis of the rotation.
     * @param z                     the z-axis of the rotation.
     * @param degree                the degree of the rotation (in degrees, not radians).
     * @param interpolationDuration the duration of the interpolation.
     * @param interpolationDelay    the delay of the interpolation.
     */
    public void transformLeft(float x, float y, float z, float degree,
                              int interpolationDuration,
                              int interpolationDelay) {
        degree = (float) Math.toRadians(degree);
        decorated.setInterpolationDuration(interpolationDuration);
        decorated.setInterpolationDelay(interpolationDelay);
        Transformation transformation = decorated.getTransformation();
        transformation.getLeftRotation()
                .set(new AxisAngle4f(degree, x, y, z));
        decorated.setTransformation(transformation);
    }

    /**
     * Will transform the itemStack to leftRotation.
     * Will use '-1' as the interpolation delay.
     *
     * @param x                     the x-axis of the rotation.
     * @param y                     the y-axis of the rotation.
     * @param z                     the z-axis of the rotation.
     * @param degree                the degree of the rotation (in degrees, not radians).
     * @param interpolationDuration the duration of the interpolation.
     */
    public void transformRight(float x, float y, float z, float degree,
                               int interpolationDuration) {
        transformRight(x, y, z, degree, interpolationDuration, -1);
    }

    /**
     * Will transform the itemStack to rightRotation.
     *
     * @param x                     the x-axis of the rotation.
     * @param y                     the y-axis of the rotation.
     * @param z                     the z-axis of the rotation.
     * @param degree                the degree of the rotation (in degrees, not radians).
     * @param interpolationDuration the duration of the interpolation.
     * @param interpolationDelay    the delay of the interpolation.
     */
    public void transformRight(float x, float y, float z, float degree,
                               int interpolationDuration,
                               int interpolationDelay) {
        degree = (float) Math.toRadians(degree);
        decorated.setInterpolationDuration(interpolationDuration);
        decorated.setInterpolationDelay(interpolationDelay);
        Transformation transformation = decorated.getTransformation();
        transformation.getRightRotation()
                .set(new AxisAngle4f(degree, x, y, z));
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
     * @param degree                the degree of the rotation (in degrees, not radians).
     * @param interpolationDuration the duration of the interpolation.
     * @param plugin                the plugin used to schedule the task
     */
    public void transformLeftPerpetual(float x, float y, float z, float degree,
                                       int interpolationDuration,
                                       JavaPlugin plugin) {
        setPerpetualAsync(plugin, 0, interpolationDuration, () -> {
            transformRight(x, y, z, degree, interpolationDuration);
        });
    }

    /**
     * Will transform the itemStack to leftRotation
     * perpetually using asynchronous tasks.
     *
     * @param x                     the x-axis of the rotation.
     * @param y                     the y-axis of the rotation.
     * @param z                     the z-axis of the rotation.
     * @param degree                the degree of the rotation (in degrees, not radians).
     * @param interpolationDuration the duration of the interpolation.
     * @param interpolationDelay    the delay of the interpolation.
     * @param plugin                the plugin used to schedule the task
     */
    public void transformLeftPerpetual(float x, float y, float z, float degree,
                                       int interpolationDuration,
                                       int interpolationDelay,
                                       JavaPlugin plugin) {
        setPerpetualAsync(plugin, 0, interpolationDuration, () -> {
            transformLeft(x, y, z, degree, interpolationDuration,
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
     * @param degree                the degree of the rotation (in degrees, not radians).
     * @param interpolationDuration the duration of the interpolation.
     * @param plugin                the plugin used to schedule the task
     */
    public void transformRightPerpetual(float x, float y, float z, float degree,
                                        int interpolationDuration,
                                        JavaPlugin plugin) {
        setPerpetualAsync(plugin, 0, interpolationDuration, () -> {
            transformRight(x, y, z, degree, interpolationDuration);
        });
    }

    /**
     * Will transform the itemStack to rightRotation
     * perpetually using asynchronous tasks.
     *
     * @param x                     the x-axis of the rotation.
     * @param y                     the y-axis of the rotation.
     * @param z                     the z-axis of the rotation.
     * @param degree                the degree of the rotation (in degrees, not radians).
     * @param interpolationDuration the duration of the interpolation.
     * @param interpolationDelay    the delay of the interpolation.
     * @param plugin                the plugin used to schedule the task
     */
    public void transformRightPerpetual(float x, float y, float z, float degree,
                                        int interpolationDuration,
                                        int interpolationDelay,
                                        JavaPlugin plugin) {
        setPerpetualAsync(plugin, 0, interpolationDuration, () -> {
            transformRight(x, y, z, degree, interpolationDuration,
                    interpolationDelay);
        });
    }
}
