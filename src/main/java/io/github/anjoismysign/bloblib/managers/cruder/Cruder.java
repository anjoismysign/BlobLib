package io.github.anjoismysign.bloblib.managers.cruder;

import io.github.anjoismysign.bloblib.psa.BukkitDatabaseProvider;
import io.github.anjoismysign.psa.crud.CrudDatabase;
import io.github.anjoismysign.psa.crud.CrudDatabaseCredentials;
import io.github.anjoismysign.psa.crud.CrudManager;
import io.github.anjoismysign.psa.crud.Crudable;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Function;

public interface Cruder<T extends Crudable> {

    static <T extends Crudable> Cruder<T> of(Plugin javaPlugin,
                                             Class<T> clazz,
                                             Function<String, T> createFunction){
        return of(javaPlugin,clazz,createFunction,null);
    }

    static <T extends Crudable> Cruder<T> of(Plugin javaPlugin,
                                             Class<T> clazz,
                                             Function<String, T> createFunction,
                                             @Nullable File directory){
        var provider = BukkitDatabaseProvider.INSTANCE;
        CrudDatabaseCredentials credentials = directory == null ?
                provider.getDatabaseProvider().of(javaPlugin)
                :
                provider.getDatabaseProvider().of(javaPlugin, directory);
        @SuppressWarnings("unchecked") CrudDatabase<T> crudDatabase = credentials.getCrudDatabaseFor(clazz);
        CrudManager<T> crudManager = crudDatabase.crudManagerOf(createFunction);
        return new Cruder<>() {
            @Override
            public boolean exists(String identification) {
                return crudManager.exists(identification);
            }

            @Override
            public @NotNull T createAndUpdate(String identification) {
                T created = crudManager.create(identification);
                crudManager.update(created);
                return created;
            }

            @Override
            public @NotNull T readOrGenerate(String identification) {
                return crudManager.read(identification);
            }

            @Override
            public void update(T instance) {
                crudManager.update(instance);
            }

            @Override
            public void delete(String identification) {
                crudManager.delete(identification);
            }
        };
    }

    boolean exists(String identification);

    @NotNull
    T createAndUpdate(String identification);

    @NotNull T readOrGenerate(String identification);

    void update(T instance);

    void delete(String identification);

}
