package io.github.anjoismysign.bloblib.managers.cruder;

import io.github.anjoismysign.psa.crud.Crudable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class BukkitCruderBuilder<T extends Crudable> {

    private JavaPlugin plugin;
    private Class<T> crudableClass;
    private @Nullable Function<String, T> createFunction = null;
    private @Nullable Function<T, Event> joinEvent;
    private @Nullable Function<T, Event> quitEvent;
    private @NotNull EventPriority joinPriority = EventPriority.NORMAL;
    private @NotNull EventPriority quitPriority = EventPriority.NORMAL;
    private @Nullable Consumer<T> onRead;
    private @Nullable Consumer<T> onUpdate;

    private T createInstance(String identification) {
        try {
            Constructor<T> constructor = crudableClass.getConstructor(String.class);
            return constructor.newInstance(identification);
        } catch (NoSuchMethodException exception) {
            throw new RuntimeException(
                    "Class " + crudableClass.getName() +
                            " must have a public constructor that accepts a single String (identification) parameter. " +
                            "This constructor is fallback for automatic instantiation by BlobCruderBuilder when" +
                            " create function is not provided.",
                    exception
            );
        } catch (InstantiationException exception) {
            throw new RuntimeException(
                    "Failed to instantiate class " + crudableClass.getName() +
                            ". The class may be abstract or an interface.",
                    exception
            );
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(
                    "Cannot access the constructor of class " + crudableClass.getName() +
                            ". Make sure the constructor is public.",
                    exception
            );
        } catch (InvocationTargetException exception) {
            throw new RuntimeException(
                    "Constructor of class " + crudableClass.getName() +
                            " threw an exception during instantiation with identification: " + identification,
                    exception.getCause()
            );
        }
    }

    /**
     * Sets the JavaPlugin instance for this BlobCruderBuilder.
     * <p>
     * The plugin instance is used to register event listeners, schedule tasks,
     * and access the plugin's data folder for storage operations.
     * </p>
     *
     * @param plugin The JavaPlugin instance to use
     * @return This builder instance for method chaining
     */
    public BukkitCruderBuilder<T> plugin(JavaPlugin plugin) {
        this.plugin = plugin;
        return this;
    }

    /**
     * Sets the class type for the Crudable objects to be managed.
     * <p>
     * This class type is used to create new instances when needed and to
     * identify the type of objects stored in the BlobCruder.
     * </p>
     *
     * @param crudableClass The class type that extends Crudable
     * @return This builder instance for method chaining
     */
    public BukkitCruderBuilder<T> crudableClass(Class<T> crudableClass) {
        this.crudableClass = crudableClass;
        return this;
    }

    /**
     * Sets the function used to create new instances of the Crudable object.
     * <p>
     * This function is called when a new instance needs to be created,
     * typically when a player joins for the first time or when data is read
     * from storage. If not set, the builder will attempt to use reflection
     * to create instances via a String constructor.
     * </p>
     *
     * @param createFunction A function that takes a String identification
     *                       and returns a new instance of T, or null to use
     *                       the default reflection-based approach
     * @return This builder instance for method chaining
     */
    public BukkitCruderBuilder<T> createFunction(@Nullable Function<String, T> createFunction) {
        this.createFunction = createFunction;
        return this;
    }

    /**
     * Sets the function used to create join events for Crudable objects.
     * <p>
     * When a player joins the server and their Crudable object is loaded,
     * this function will be used to create a custom event that gets called.
     * The event can be used to notify other parts of the plugin that the
     * player's data has been loaded.
     * </p>
     *
     * @param joinEvent A function that takes a Crudable object and returns
     *                  a Bukkit Event to be called on player join, or null
     *                  if no event should be fired
     * @return This builder instance for method chaining
     */
    public BukkitCruderBuilder<T> joinEvent(@Nullable Function<T, Event> joinEvent) {
        this.joinEvent = joinEvent;
        return this;
    }

    /**
     * Sets the function used to create quit events for Crudable objects.
     * <p>
     * When a player quits the server, this function will be used to create
     * a custom event that gets called before their data is saved. The event
     * can be used to notify other parts of the plugin that the player's data
     * is about to be saved and the player is leaving.
     * </p>
     *
     * @param quitEvent A function that takes a Crudable object and returns
     *                  a Bukkit Event to be called on player quit, or null
     *                  if no event should be fired
     * @return This builder instance for method chaining
     */
    public BukkitCruderBuilder<T> quitEvent(@Nullable Function<T, Event> quitEvent) {
        this.quitEvent = quitEvent;
        return this;
    }

    /**
     * Sets the event priority for the player join listener.
     * <p>
     * This priority determines when the join event handling occurs relative
     * to other plugins listening to the same events. The default priority is
     * {@link EventPriority#NORMAL}.
     * </p>
     *
     * @param joinPriority The event priority for player join handling
     * @return This builder instance for method chaining
     */
    public BukkitCruderBuilder<T> joinPriority(@NotNull EventPriority joinPriority) {
        this.joinPriority = joinPriority;
        return this;
    }

    /**
     * Sets the event priority for the player quit listener.
     * <p>
     * This priority determines when the quit event handling occurs relative
     * to other plugins listening to the same events. The default priority is
     * {@link EventPriority#NORMAL}.
     * </p>
     *
     * @param quitPriority The event priority for player quit handling
     * @return This builder instance for method chaining
     */
    public BukkitCruderBuilder<T> quitPriority(@NotNull EventPriority quitPriority) {
        this.quitPriority = quitPriority;
        return this;
    }

    /**
     * Sets the consumer function to be executed when a Crudable object is read.
     * <p>
     * This consumer is called after a Crudable object is loaded from storage
     * (either by reading existing data or generating new data). It can be used
     * for post-processing or initialization of the loaded object.
     * </p>
     * <p>
     * <b>Threading Note:</b> This consumer may be called both synchronously
     * (on the main thread) and asynchronously (on a background thread).
     * You must manually check the thread context using {@code Bukkit.isPrimaryThread()}
     * and handle thread-sensitive operations accordingly.
     * </p>
     *
     * @param onRead A consumer that processes the Crudable object after reading,
     *               or null if no post-read processing is needed
     * @return This builder instance for method chaining
     */
    public BukkitCruderBuilder<T> onRead(@Nullable Consumer<T> onRead) {
        this.onRead = onRead;
        return this;
    }

    /**
     * Sets the consumer function to be executed when a Crudable object is updated.
     * <p>
     * This consumer is called after a Crudable object is saved to storage.
     * It can be used for post-save operations or notifications.
     * </p>
     * <p>
     * <b>Threading Note:</b> This consumer may be called both synchronously
     * (on the main thread) and asynchronously (on a background thread).
     * You must manually check the thread context using {@code Bukkit.isPrimaryThread()}
     * and handle thread-sensitive operations accordingly.
     * </p>
     *
     * @param onUpdate A consumer that processes the Crudable object after updating,
     *                 or null if no post-update processing is needed
     * @return This builder instance for method chaining
     */
    public BukkitCruderBuilder<T> onUpdate(@Nullable Consumer<T> onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }

    /**
     * Builds and returns a new BlobCruder instance with the configured settings.
     * <p>
     * This method creates a BlobCruder that manages Crudable objects of the specified type.
     * All required parameters (plugin and crudableClass) must be set before calling this method.
     * Optional parameters will use their default values if not set:
     * </p>
     * <ul>
     *   <li>createFunction: Uses reflection-based instantiation if not provided</li>
     *   <li>joinEvent: No custom event fired on join if not provided</li>
     *   <li>quitEvent: No custom event fired on quit if not provided</li>
     *   <li>joinPriority: {@link EventPriority#NORMAL} if not provided</li>
     *   <li>quitPriority: {@link EventPriority#NORMAL} if not provided</li>
     *   <li>onRead: No post-read processing if not provided</li>
     *   <li>onUpdate: No post-update processing if not provided</li>
     * </ul>
     *
     * @return A new BlobCruder instance configured with the builder's settings
     * @throws NullPointerException if plugin or crudableClass is null
     */
    public BukkitCruder<T> build() {
        Objects.requireNonNull(plugin, "'plugin' cannot be null");
        Objects.requireNonNull(crudableClass, "'crudableClass' cannot be null");
        createFunction = createFunction == null ? this::createInstance : createFunction;
        return new BukkitCruder<>(
                plugin,
                crudableClass,
                createFunction,
                joinEvent,
                quitEvent,
                joinPriority,
                quitPriority,
                onRead,
                onUpdate
        );
    }

}
