package us.mytheria.bloblib.jlib.storage.database.sqlite;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import us.mytheria.bloblib.jlib.methods.General;
import us.mytheria.bloblib.jlib.storage.StorageAction;
import us.mytheria.bloblib.jlib.storage.database.SQLDatabase;

import java.io.File;
import java.sql.Connection;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/11/15
 */
public final class SQLite extends SQLDatabase {

    /**
     * Constructs a new SQLite instance, shouldn't be used externally, use {@link SQLiteLoader} instead
     * @param plugin The JavaPlugin associated with the SQLite Database
     * @param name The name of the SQLite file
     */
    SQLite(JavaPlugin plugin, String name) {
        super(plugin, null, 0, name, null, null);
        File file = new File(this.plugin.getDataFolder(), this.name.endsWith(".db")?this.name:this.name + ".db");
        try {
            if (!file.exists()) file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.dataSource.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath());
    }

    /**
     * Returns the Connection for the SQLite Database
     * @return The Connection
     * @see Connection
     */
    @Override
    public Connection getConnection() {
        StorageAction storageAction = new StorageAction(StorageAction.Type.SQLITE_GETCONNECTION, this.name);
        Connection c = null;
        try {
            c = this.dataSource.getConnection();
            storageAction.setSuccess(true);
        } catch (Exception e) {
            General.sendColoredMessage(this.plugin, "Failed to connect to the SQLite Database using " + this.name + '!', ChatColor.RED);
            storageAction.setSuccess(false);
        }
        this.actions.add(storageAction);
        return c;
    }
}
