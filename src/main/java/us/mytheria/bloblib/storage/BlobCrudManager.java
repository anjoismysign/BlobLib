package us.mytheria.bloblib.storage;

import me.anjoismysign.anjo.crud.CrudManager;
import me.anjoismysign.anjo.logger.Logger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.BlobCrudable;

public class BlobCrudManager<T extends BlobCrudable> implements CrudManager<T> {
    private final CrudManager<T> crudManager;
    private final StorageType storageType;
    private final IdentifierType identifierType;

    public BlobCrudManager(CrudManager<T> crudManager, StorageType storageType, IdentifierType identifierType) {
        this.crudManager = crudManager;
        this.storageType = storageType;
        this.identifierType = identifierType;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public IdentifierType getIdentifierType() {
        return identifierType;
    }

    @Override
    public boolean exists(String s) {
        return crudManager.exists(s);
    }

    public boolean exists(Player player) {
        if (identifierType == IdentifierType.UUID)
            return exists(player.getUniqueId().toString());
        else if (identifierType == IdentifierType.PLAYERNAME)
            return exists(player.getName());
        else
            throw new IllegalStateException("IdentifierType is not set to UUID or PLAYERNAME: " +
                    identifierType);
    }

    @Override
    public T create(String s) {
        return crudManager.create(s);
    }

    public T create(Player player) {
        if (identifierType == IdentifierType.UUID)
            return create(player.getUniqueId().toString());
        else if (identifierType == IdentifierType.PLAYERNAME)
            return create(player.getName());
        else
            throw new IllegalStateException("IdentifierType is not set to UUID or PLAYERNAME: " +
                    identifierType);
    }

    @NotNull
    @Override
    public T read(String s) {
        return crudManager.read(s);
    }

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

    @Nullable
    @Override
    public T readOrNull(String s) {
        return crudManager.readOrNull(s);
    }

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

    @Override
    public void update(T t) {
        crudManager.update(t);
    }

    @Override
    public void delete(String s) {
        crudManager.delete(s);
    }

    public void delete(Player player) {
        if (identifierType == IdentifierType.UUID)
            delete(player.getUniqueId().toString());
        else if (identifierType == IdentifierType.PLAYERNAME)
            delete(player.getName());
        else
            throw new IllegalStateException("IdentifierType is not set to UUID or PLAYERNAME: " +
                    identifierType);
    }

    @Nullable
    @Override
    public Logger getLogger() {
        return crudManager.getLogger();
    }
}
