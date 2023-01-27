package us.mytheria.bloblib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import us.mytheria.bloblib.BlobLib;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anjoismysign
 * For personal use inside BlobLib.
 * You are not meant to make a new instance
 * of this class...
 */
public class BlobLibCmd implements CommandExecutor, TabCompleter {
    private final BlobLib main;

    /**
     * Creates a new instance of BlobLibCmd
     */
    public BlobLibCmd() {
        this.main = BlobLib.getInstance();
        main.getCommand("bloblib").setExecutor(this);
        main.getCommand("bloblib").setTabCompleter(this);
    }

    /**
     * Will be called when '/bloblib' is executed
     *
     * @param sender The CommandSender
     * @param cmd    The command
     * @param label  The label used
     * @param args   The arguments
     * @return true if the command was executed successfully
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("bloblib.admin")) {
            main.getMessageManager().getMessage("System.No-Permission").toCommandSender(sender);
            return true;
        }
        if (args.length < 1) {
            debug(sender);
            return true;
        }
        String arg1 = args[0];
        if (arg1.equalsIgnoreCase("reload")) {
            main.reload();
            main.getMessageManager().getMessage("System.Reload").toCommandSender(sender);
            return true;
        }
        return false;
    }

    /**
     * Will send debug information to CommandSender
     *
     * @param sender The CommandSender
     */
    public void debug(CommandSender sender) {
        if (sender.hasPermission("bloblib.debug")) {
            sender.sendMessage("/bloblib reload");
        }
    }

    /**
     * Tab complete for /area
     *
     * @param sender  The sender of the command
     * @param command The command
     * @param alias   The alias used
     * @param args    The arguments
     * @return The list of possible completions
     */
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("bloblib")) {
            if (sender.hasPermission("blobblib.admin")) {
                List<String> l = new ArrayList<>();
                if (args.length == 1) {
                    l.add("reload");
                }
                return l;
            }
        }
        return null;
    }
}
