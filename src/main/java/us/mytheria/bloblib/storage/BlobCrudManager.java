package us.mytheria.bloblib.storage;

import me.anjoismysign.anjo.crud.CrudManager;
import me.anjoismysign.anjo.logger.Logger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.BlobCrudable;

import java.util.UUID;

public class BlobCrudManager<T extends BlobCrudable> implements CrudManager<T> {
    private final CrudManager<T> crudManager;
    private final StorageType storageType;
    private final IdentifierType identifierType;

    public BlobCrudManager(CrudManager<T> crudManager,
                           StorageType storageType,
                           IdentifierType identifierType) {
        this.crudManager = crudManager;
        this.storageType = storageType;
        this.identifierType = identifierType;
    }

    /**
     * Returns the storage type of the BlobCrudManager.
     *
     * @return The storage type of the BlobCrudManager.
     */
    public StorageType getStorageType() {
        return storageType;
    }

    /**
     * Returns the identifier type of the BlobCrudManager.
     *
     * @return The identifier type of the BlobCrudManager.
     */
    public IdentifierType getIdentifierType() {
        return identifierType;
    }

    /**
     * Returns whether the BlobCrudable with the specified identification exists.
     *
     * @param s The identification of the BlobCrudable.
     * @return Whether the BlobCrudable with the specified identification exists.
     */
    @Override
    public boolean exists(String s) {
        return crudManager.exists(s);
    }

    /**
     * Returns whether the BlobCrudable with the identification of the player exists.
     *
     * @param player The player to check the BlobCrudable for.
     * @return Whether the BlobCrudable with the identification of the player exists.
     */
    public boolean exists(Player player) {
        if (identifierType == IdentifierType.UUID)
            return exists(player.getUniqueId().toString());
        else if (identifierType == IdentifierType.PLAYERNAME)
            return exists(player.getName());
        else
            throw new IllegalStateException("IdentifierType is not set to UUID or PLAYERNAME: " +
                    identifierType);
    }

    /**
     * Creates new BlobCrudable with the given identification and returns it.
     *
     * @param s The identification of the BlobCrudable.
     * @return The created BlobCrudable.
     */
    @Override
    public T create(String s) {
        return crudManager.create(s);
    }

    /**
     * Creates new BlobCrudable with the given identification and returns it.
     *
     * @param identification The identification of the BlobCrudable.
     * @return The created BlobCrudable.
     */
    @NotNull
    public T createOrFail(String identification) {
        if (crudManager.exists(identification))
            throw new IllegalStateException("Identifier already exists: " + identification);
        return crudManager.create(identification);
    }

    /**
     * Creates new BlobCrudable with a random identification and returns it.
     * Identification is a random UUID. If BlobCrudable was lucky enough
     * to generate an existing UUID, it will throw an IllegalStateException.
     *
     * @return The created BlobCrudable.
     */
    @NotNull
    public T createRandomOrFail() {
        String identification = UUID.randomUUID().toString();
        return createOrFail(identification);
    }

    /**
     * Will create a new BlobCrudable with the identification of the player.
     * It's based on the IdentifierType from the BlobCrudManager.
     *
     * @param player The player to create the BlobCrudable for.
     * @return The created BlobCrudable.
     */
    public T create(Player player) {
        if (identifierType == IdentifierType.UUID)
            return create(player.getUniqueId().toString());
        else if (identifierType == IdentifierType.PLAYERNAME)
            return create(player.getName());
        else
            throw new IllegalStateException("IdentifierType is not set to UUID or PLAYERNAME: " +
                    identifierType);
    }

    /**
     * Will read the BlobCrudable with the specified identification.
     *
     * @param s The identification of the BlobCrudable.
     * @return The BlobCrudable.
     */
    @NotNull
    @Override
    public T read(String s) {
        return crudManager.read(s);
    }

    /**
     * Will read the BlobCrudable with the identification of the player.
     * It will assume that identification type matches the IdentifierType
     *
     * @param player The player to read the BlobCrudable for.
     * @return The BlobCrudable.
     */
    @NotNull
    public T read(Player player) {
        if (identifierType == IdentifierType.UUID)
            return read(player.getUniqueId().toString());
        else if (identifierType == IdentifierType.PLAYERNAME)
            return read(player.getName());
        else
            throw new IllegalStateException("IdentifierType is not set to UUID or PLAYERNAME: " +
                    identifierType);
    }

    /**
     * Will read the BlobCrudable with the specified identification.
     *
     * @param s The identification of the BlobCrudable.
     * @return The BlobCrudable or null if it doesn't exist.
     */
    @Nullable
    @Override
    public T readOrNull(String s) {
        return crudManager.readOrNull(s);
    }

    /**
     * Will read the BlobCrudable with the identification of the player.
     *
     * @param player The player to read the BlobCrudable for.
     * @return The BlobCrudable or null if it doesn't exist.
     */
    @Nullable
    public T readOrNull(Player player) {
        if (identifierType == IdentifierType.UUID)
            return readOrNull(player.getUniqueId().toString());
        else if (identifierType == IdentifierType.PLAYERNAME)
            return readOrNull(player.getName());
        else
            throw new IllegalStateException("IdentifierType is not set to UUID or PLAYERNAME: " +
                    identifierType);
    }

    /**
     * Will update the BlobCrudable.
     *
     * @param t The BlobCrudable to update.
     */
    @Override
    public void update(T t) {
        crudManager.update(t);
    }

    /**
     * Will delete the BlobCrudable with the specified identification.
     *
     * @param s The identification of the BlobCrudable.
     */
    @Override
    public void delete(String s) {
        crudManager.delete(s);
    }

    /**
     * Will delete the BlobCrudable with the identification of the player.
     *
     * @param player The player to delete the BlobCrudable for.
     */
    public void delete(Player player) {
        if (identifierType == IdentifierType.UUID)
            delete(player.getUniqueId().toString());
        else if (identifierType == IdentifierType.PLAYERNAME)
            delete(player.getName());
        else
            throw new IllegalStateException("IdentifierType is not set to UUID or PLAYERNAME: " +
                    identifierType);
    }

    /**
     * Will return the logger of the CrudManager.
     *
     * @return The logger of the CrudManager.
     */
    @Nullable
    @Override
    public Logger getLogger() {
        return crudManager.getLogger();
    }
}
