package us.mytheria.bloblib.entities.currency;

import org.bukkit.event.Event;
import us.mytheria.bloblib.entities.BlobCrudable;
import us.mytheria.bloblib.entities.ObjectDirector;
import us.mytheria.bloblib.entities.ObjectDirectorData;
import us.mytheria.bloblib.managers.ManagerDirector;

import java.util.function.Function;

public class EconomyFactory {

    /**
     * Creates a new ObjectDirector (of type Currency).
     * No commands or tab completions are added.
     *
     * @param managerDirector The ManagerDirector
     */
    public static ObjectDirector<Currency> CURRENCY_DIRECTOR(ManagerDirector managerDirector) {
        ObjectDirector<Currency> director = new ObjectDirector<>(managerDirector, ObjectDirectorData.simple(managerDirector.getFileManager(),
                "Currency"), Currency::fromFile);
        director.getBuilderManager().setBuilderBiFunction(
                CurrencyBuilder::build);
        return director;
    }

    /**
     * Creates a new WalletOwnerManager that doesn't register and call join and quit events for players.
     * It also doesn't register any commands or tab completions.
     *
     * @param managerDirector The ManagerDirector
     * @param newBorn         A function that by passing a UUID, it will fill a BlobCrudable
     *                        with default key-value pairs.
     *                        This is used to create new/fresh WalletOwners.
     * @param walletOwner     A function that by passing a BlobCrudable, it will return a WalletOwner.
     *                        WalletOwners use this to store their data inside databases.
     * @param crudableName    The name of the BlobCrudable. This will be used for
     *                        as the column name in the database.
     * @param logActivity     Whether to log activity in the console.
     * @param <T>             The type of WalletOwner.
     * @return A new WalletOwnerManager.
     */
    public static <T extends WalletOwner> WalletOwnerManager<T> SIMPLE_WALLET_OWNER_MANAGER(ManagerDirector managerDirector,
                                                                                            Function<BlobCrudable, BlobCrudable> newBorn,
                                                                                            Function<BlobCrudable, T> walletOwner,
                                                                                            String crudableName, boolean logActivity) {
        return new <T>WalletOwnerManager<T>(managerDirector, newBorn, walletOwner, crudableName, logActivity, null, null);
    }

    /**
     * Creates a new WalletOwnerManager that will register and call custom join and quit events for players.
     * It also doesn't register any commands or tab completions.
     *
     * @param managerDirector The ManagerDirector
     * @param newBorn         A function that by passing a UUID, it will fill a BlobCrudable
     *                        with default key-value pairs.
     *                        This is used to create new/fresh WalletOwners.
     * @param walletOwner     A function that by passing a BlobCrudable, it will return a WalletOwner.
     *                        WalletOwners use this to store their data inside databases.
     * @param crudableName    The name of the BlobCrudable. This will be used for
     *                        as the column name in the database.
     * @param logActivity     Whether to log activity in the console.
     * @param joinEvent       A function that by passing a WalletOwner, it will return a join event.
     *                        It's called SYNCHRONOUSLY.
     *                        It's called when a player joins the server.
     * @param quitEvent       A function that by passing a WalletOwner, it will return a quit event.
     *                        It's called SYNCHRONOUSLY.
     *                        It's called when a player quits/leaves the server.
     * @param <T>             The type of WalletOwner.
     * @return A new WalletOwnerManager.
     */
    public static <T extends WalletOwner> WalletOwnerManager<T> WALLET_OWNER_MANAGER(ManagerDirector managerDirector,
                                                                                     Function<BlobCrudable, BlobCrudable> newBorn,
                                                                                     Function<BlobCrudable, T> walletOwner,
                                                                                     String crudableName, boolean logActivity,
                                                                                     Function<T, Event> joinEvent,
                                                                                     Function<T, Event> quitEvent) {
        return new <T>WalletOwnerManager<T>(managerDirector, newBorn, walletOwner, crudableName, logActivity, joinEvent, quitEvent);
    }
}
