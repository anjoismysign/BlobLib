package us.mytheria.bloblib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import us.mytheria.bloblib.BlobLib;

import java.util.ArrayList;
import java.util.List;

public class BlobLibCmd implements CommandExecutor, TabCompleter {
    private final BlobLib main;

    public BlobLibCmd() {
        this.main = BlobLib.getInstance();
        main.getCommand("bloblib").setExecutor(this);
        main.getCommand("bloblib").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("blobgrind.admin")) {
            main.getMessageManager().getMessage("System.No-Permission").toCommandSender(sender);
            return true;
        }
        if (args.length < 1) {
            debug(sender);
            return true;
        }
        String arg1 = args[0];
        if (arg1.equalsIgnoreCase("reload")) {
            main.getMessageManager().reload();
            main.getSoundManager().reload();
            main.getMessageManager().getMessage("System.Reload").toCommandSender(sender);
            return true;
        }
        return false;
    }

    public void debug(CommandSender sender) {
        if (sender.hasPermission("blobgrind.debug")) {
            sender.sendMessage("/area add");
            sender.sendMessage("/area manager");
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("area")) {
            if (sender.hasPermission("blobgrind.admin")) {
                List<String> l = new ArrayList<>();
                if (args.length == 1) {
                    l.add("manager");
                    l.add("add");
                }
                return l;
            }
        }
        return null;
    }
}
