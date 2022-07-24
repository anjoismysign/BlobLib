package us.mytheria.bloblib.jlib.storage.database.sqlite;

import org.bukkit.plugin.java.JavaPlugin;
import us.mytheria.bloblib.jlib.storage.database.SQLLoader;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/11/15
 */
public class SQLiteLoader extends SQLLoader {
    /**
     * Constructs a new SQLiteLoader, use this by extending the SQLiteLoader
     * @param plugin The JavaPlugin associated with the SQLite Database
     * @param name The name of the SQLite file
     * @see SQLite
     */
    public SQLiteLoader(JavaPlugin plugin, String name) {
        super(new SQLite(plugin, name));
    }
}
