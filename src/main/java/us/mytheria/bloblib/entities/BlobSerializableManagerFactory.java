package us.mytheria.bloblib.entities;

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.managers.ManagerDirector;

import java.util.function.Function;

public class BlobSerializableManagerFactory {
    /**
     * Creates a simple BlobSerializableManager that does not listen to events
     * and doesn't log activity in console regarding the crud operations.
     *
     * @param managerDirector The manager director
     * @param generator       The generator function
     * @param crudableName    The crudable name
     * @param <T>             The type of the blob serializable
     * @return The blob serializable manager
     */
    public static <T extends BlobSerializable> BlobSerializableManager<T> SIMPLE(ManagerDirector managerDirector,
                                                                                 Function<BlobCrudable, T> generator,
                                                                                 String crudableName) {
        return SIMPLE(managerDirector, generator, crudableName, false);
    }

    /**
     * Creates a simple BlobSerializableManager that does not listen to events.
     *
     * @param managerDirector The manager director
     * @param generator       The generator function
     * @param crudableName    The crudable name
     * @param logActivity     Whether to log activity in console regarding the crud operations
     * @param <T>             The type of the blob serializable
     * @return The blob serializable manager
     */
    public static <T extends BlobSerializable> BlobSerializableManager<T> SIMPLE(ManagerDirector managerDirector,
                                                                                 Function<BlobCrudable, T> generator,
                                                                                 String crudableName,
                                                                                 boolean logActivity) {
        return new BlobSerializableManager<>(managerDirector,
                crudable -> crudable, generator, crudableName,
                logActivity, null, null);
    }

    /**
     * Creates a simple BlobSerializableManager that allows
     * listening to custom events. Does not log activity in console
     * regarding the crud operations.
     *
     * @param managerDirector The manager director
     * @param generator       The generator function
     * @param crudableName    The crudable name
     * @param joinEvent       The join event.
     *                        Function consumes the BlobSerializable
     *                        related in the event and needs to return
     *                        the event to be called.
     * @param quitEvent       The quit event.
     *                        Function consumes the BlobSerializable
     *                        related in the event and needs to return
     *                        the event to be called.
     * @param <T>             The type of the blob serializable
     * @return The blob serializable manager
     */
    public static <T extends BlobSerializable> BlobSerializableManager<T> LISTENER(ManagerDirector managerDirector,
                                                                                   Function<BlobCrudable, T> generator,
                                                                                   String crudableName,
                                                                                   @Nullable Function<T, Event> joinEvent,
                                                                                   @Nullable Function<T, Event> quitEvent) {
        return LISTENER(managerDirector, generator, crudableName, false, joinEvent, quitEvent);
    }

    /**
     * Creates a simple BlobSerializableManager that allows
     * listening to custom events.
     *
     * @param managerDirector The manager director
     * @param generator       The generator function
     * @param crudableName    The crudable name
     * @param logActivity     Whether to log activity in console regarding the crud operations
     * @param joinEvent       The join event.
     *                        Function consumes the BlobSerializable
     *                        related in the event and needs to return
     *                        the event to be called.
     * @param quitEvent       The quit event.
     *                        Function consumes the BlobSerializable
     *                        related in the event and needs to return
     *                        the event to be called.
     * @param <T>             The type of the blob serializable
     * @return The blob serializable manager
     */
    public static <T extends BlobSerializable> BlobSerializableManager<T> LISTENER(ManagerDirector managerDirector,
                                                                                   Function<BlobCrudable, T> generator,
                                                                                   String crudableName,
                                                                                   boolean logActivity,
                                                                                   @Nullable Function<T, Event> joinEvent,
                                                                                   @Nullable Function<T, Event> quitEvent) {
        return new BlobSerializableManager<>(managerDirector,
                crudable -> crudable, generator, crudableName,
                logActivity, joinEvent, quitEvent);
    }

    /**
     * Creates a complex BlobSerializableManager that allows
     * listening to custom events and allows to create a new
     * BlobCrudable when the BlobSerializable is not found.
     * It also allows passing a newBorn function which autofills
     * the BlobCrudable with default values whenever
     * a user is found as a new user (never joined before).
     *
     * @param managerDirector The manager director
     * @param newBorn         The newborn function.
     * @param generator       The generator function
     * @param crudableName    The crudable name
     * @param logActivity     Whether to log activity in console regarding the crud operations
     * @param joinEvent       The join event.
     *                        Function consumes the BlobSerializable
     *                        related in the event and needs to return
     * @param quitEvent       The quit event.
     *                        Function consumes the BlobSerializable
     *                        related in the event and needs to return
     * @param <T>             The type of the blob serializable
     * @return The blob serializable manager
     */
    public static <T extends BlobSerializable> BlobSerializableManager<T> COMPLEX(ManagerDirector managerDirector,
                                                                                  Function<BlobCrudable, BlobCrudable> newBorn,
                                                                                  Function<BlobCrudable, T> generator,
                                                                                  String crudableName,
                                                                                  boolean logActivity,
                                                                                  @Nullable Function<T, Event> joinEvent,
                                                                                  @Nullable Function<T, Event> quitEvent) {
        return new BlobSerializableManager<>(managerDirector, newBorn, generator, crudableName, logActivity, joinEvent, quitEvent);
    }
}
