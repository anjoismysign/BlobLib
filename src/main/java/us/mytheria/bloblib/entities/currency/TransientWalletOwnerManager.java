package us.mytheria.bloblib.entities.currency;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.BlobCrudable;
import us.mytheria.bloblib.managers.ManagerDirector;

import java.util.function.Function;

public class TransientWalletOwnerManager<T extends WalletOwner> extends WalletOwnerManager<T> {
    protected TransientWalletOwnerManager(ManagerDirector managerDirector,
                                          Function<BlobCrudable, BlobCrudable> newBorn,
                                          Function<BlobCrudable, T> generator,
                                          String crudableName,
                                          boolean logActivity,
                                          @Nullable Function<T, Event> joinEvent,
                                          @Nullable Function<T, Event> quitEvent,
                                          @NotNull EventPriority joinPriority,
                                          @NotNull EventPriority quitPriority) {
        super(managerDirector, newBorn, generator, crudableName, logActivity, joinEvent, quitEvent,
                joinPriority, quitPriority);
    }

    @Override
    public BlobCrudable read(String key) {
        return new BlobCrudable(key);
    }

    @Override
    public void update(BlobCrudable crudable) {
    }

    @Override
    public boolean exists(String key) {
        return true;
    }
}
