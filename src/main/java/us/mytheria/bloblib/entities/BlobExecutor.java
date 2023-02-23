package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Result;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.managers.BlobPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Level;

public class BlobExecutor implements CommandExecutor, TabCompleter {
    private final String debugPermission;
    private final String adminPermission;
    private final String commandName;
    private BiFunction<CommandSender, String[], List<String>> tabCompleter;
    private BiFunction<CommandSender, String[], Boolean> command;
    private Consumer<CommandSender> debug;
    private final List<String> aliases;

    public BlobExecutor(BlobPlugin plugin, String commandName) {
        commandName = commandName.toLowerCase();
        this.commandName = commandName;
        PluginCommand command = plugin.getCommand(commandName);
        if (command == null)
            throw new IllegalArgumentException("Command '" + commandName + "' not found in plugin.yml");
        command.setExecutor(this);
        command.setTabCompleter(this);
        this.adminPermission = plugin.getName().toLowerCase() + ".admin";
        this.debugPermission = plugin.getName().toLowerCase() + ".debug";
        tabCompleter = (sender, args) -> {
            List<String> list = new ArrayList<>();
            list.add("By default, this command has no tab completion.");
            list.add("Use BlobExecutor#setTabCompleter to customize.");
            list.add("If you dont want any tab completer,");
            list.add("use BlobExecutor#setTabCompleter(null)");
            return list;
        };
        this.command = (sender, args) -> {
            sender.sendMessage("By default, this command does nothing.");
            sender.sendMessage("Use BlobExecutor#setCommand to customize.");
            return false;
        };
        debug = sender -> {
            if (sender.hasPermission(debugPermission)) {
                sender.sendMessage("/" + this.commandName);
            }
        };
        this.aliases = command.getAliases().stream().map(String::toLowerCase).toList();
    }

    /**
     * This method is called when a player executes a command.
     *
     * @param command CommandSender is the sender of the command.
     *                Could be from console to even a Player.
     *                String[] are the arguments of the command.
     *                return true if the command was executed successfully,
     *                false otherwise.
     */
    public void setCommand(BiFunction<CommandSender, String[], Boolean> command) {
        this.command = Objects.requireNonNull(command, "'command' cannot be null");
    }

    /**
     * It's already implied that the commandName executed equalsIgnoreCase to the commandName
     * of this class. You don't need to check it.
     * You just start checking if it's a child command, then if it's a child command
     * of a child command and such (by using String[] args).
     *
     * @param tabCompleter CommandSender is the sender of the command.
     *                     Could be from console to even a Player.
     *                     String[] are the arguments of the command.
     *                     List&lt;String&gt; is the list of suggestions of tab completion.
     */
    public void setTabCompleter(BiFunction<CommandSender, String[], List<String>> tabCompleter) {
        this.tabCompleter = Objects.requireNonNullElseGet(tabCompleter, () -> (sender, args)
                -> new ArrayList<>());
    }

    /**
     * Will set a debug message that's run through BlobExecutor#debug(CommandSender).
     *
     * @param debug Consumer<CommandSender> is the sender of the command.
     */
    public void setDebug(Consumer<CommandSender> debug) {
        this.debug = debug;
    }

    /**
     * Will retrieve the admin permission of this command.
     *
     * @return The admin permission of this command.
     */
    public String getAdminPermission() {
        return adminPermission;
    }

    /**
     * Will retrieve the debug permission of this command.
     *
     * @return The debug permission of this command.
     */
    public String getDebugPermission() {
        return debugPermission;
    }

    /**
     * Will check if CommandSender has the admin permission of this command.
     * If not, will automatically send a ReferenceBlobMessage with the key of blobMessageKey.
     *
     * @param sender The CommandSender to check.
     * @return True if CommandSender has the admin permission of this command.
     */
    public boolean hasAdminPermission(CommandSender sender, String blobMessageKey) {
        return hasPermission(sender, adminPermission, blobMessageKey);
    }

    /**
     * Will check if CommandSender has the admin permission of this command.
     * If not, will automatically send a ReferenceBlobMessage with the key of "System.No-Permission".
     *
     * @param sender The CommandSender to check.
     * @return True if CommandSender has the admin permission of this command.
     */
    public boolean hasAdminPermission(CommandSender sender) {
        return hasAdminPermission(sender, "System.No-Permission");
    }

    /**
     * Will check if CommandSender has the debug permission of this command.
     * If not and if blobMessageKey is not null, will automatically send a
     * ReferenceBlobMessage with the key of blobMessageKey.
     *
     * @param sender         The CommandSender to check.
     * @param blobMessageKey The key of the ReferenceBlobMessage to send if CommandSender does not have the provided permission.
     * @return True if CommandSender has the debug permission of this command.
     */
    public boolean hasDebugPermission(CommandSender sender, @Nullable String blobMessageKey) {
        if (blobMessageKey != null)
            return hasPermission(sender, debugPermission, blobMessageKey);
        return sender.hasPermission(debugPermission);
    }

    /**
     * Will check if CommandSender has the debug permission of this command.
     * Won't send any BlobMessage if CommandSender does not have the debug permission.
     *
     * @param sender The CommandSender to check.
     * @return True if CommandSender has the debug permission of this command.
     */
    public boolean hasDebugPermission(CommandSender sender) {
        return hasDebugPermission(sender, null);
    }

    /**
     * Will check if CommandSender has the provided permission.
     * If not, will automatically send a ReferenceBlobMessage with the key of blobMessageKey.
     *
     * @param sender         The CommandSender to check.
     * @param permission     The permission to check.
     * @param blobMessageKey The key of the ReferenceBlobMessage to send if CommandSender does not have the provided permission.
     * @return True if CommandSender has the provided permission.
     */
    public boolean hasPermission(CommandSender sender,
                                 String permission,
                                 String blobMessageKey) {
        boolean has = sender.hasPermission(permission);
        if (!has)
            BlobLibAssetAPI.getMessage(blobMessageKey).toCommandSender(sender);
        return has;
    }

    /**
     * Will check if CommandSender has the provided permission.
     * If not, will automatically send a ReferenceBlobMessage with the key of "System.No-Permission".
     *
     * @param sender     The CommandSender to check.
     * @param permission The permission to check.
     * @return True if CommandSender has the provided permission.
     */
    public boolean hasPermission(CommandSender sender,
                                 String permission) {
        return hasPermission(sender, permission, "System.No-Permission");
    }

    /**
     * Will check of CommandSender is instance of Player.
     * If not, will automatically send a ReferenceBlobMessage with the key of blobMessageKey.
     *
     * @param sender         The CommandSender to check.
     * @param blobMessageKey The key of the ReferenceBlobMessage to send if CommandSender is not instance of Player.
     * @return True if CommandSender is instance of Player.
     */
    public boolean isInstanceOfPlayer(CommandSender sender, String blobMessageKey) {
        if (!(sender instanceof Player player)) {
            BlobLibAssetAPI.getMessage(blobMessageKey).toCommandSender(sender);
            return false;
        }
        return true;
    }

    /**
     * Will check of CommandSender is instance of Player.
     * If not, will automatically send a ReferenceBlobMessage with the
     * key of "System.Console-Not-Allowed-Command".
     *
     * @param sender The CommandSender to check.
     * @return True if CommandSender is instance of Player.
     */
    public boolean isInstanceOfPlayer(CommandSender sender) {
        return isInstanceOfPlayer(sender, "System.Console-Not-Allowed-Command");
    }

    /**
     * Will check of CommandSender is instance of Player.
     * If so, will accept the provided Consumer.
     *
     * @param sender   The CommandSender to check.
     * @param consumer The Consumer to accept if CommandSender is instance of Player.
     */
    public boolean ifInstanceOfPlayer(CommandSender sender, Consumer<Player> consumer) {
        boolean is = isInstanceOfPlayer(sender);
        if (is)
            consumer.accept((Player) sender);
        return is;
    }

    /**
     * Will check of CommandSender is instance of Player.
     * If so, will accept the provided Consumer.
     * If not, will automatically send a ReferenceBlobMessage with the key of blobMessageKey.
     *
     * @param sender         The CommandSender to check.
     * @param blobMessageKey The key of the ReferenceBlobMessage to send if CommandSender is not instance of Player.
     * @param consumer       The Consumer to accept if CommandSender is instance of Player.
     */
    public void ifInstanceOfPlayer(CommandSender sender, String blobMessageKey, Consumer<Player> consumer) {
        if (!isInstanceOfPlayer(sender, blobMessageKey))
            return;
        consumer.accept((Player) sender);
    }

    /**
     * Will check if inputted arguments are less than 1.
     * If so, will automatically send a debug message to CommandSender.
     *
     * @param args   The arguments to check.
     * @param sender The CommandSender to send the debug message to.
     * @return True if inputted arguments are less than 1.
     */
    public boolean hasNoArguments(CommandSender sender, String[] args) {
        if (args.length < 1) {
            debug(sender);
            return true;
        }
        return false;
    }

    /**
     * Will send a debug message to CommandSender.
     *
     * @param sender The CommandSender to send the debug message to.
     */
    public void debug(CommandSender sender) {
        debug.accept(sender);
    }

    /**
     * Will attempt to retrieve a child command.
     * If the child command does not exist, because arguments
     * the child command is not the same as inputted, it
     * will return an invalid result. Otherwise, will
     * provide a valid result.
     * ALWAYS CHECK IF THE RESULT IS VALID BEFORE USING IT!
     *
     * @param childCommand The child command to retrieve.
     * @param args         The arguments to check.
     * @return A Result containing the child command if it exists.
     */
    public Result<BlobChildCommand> isChildCommand(String childCommand,
                                                   String[] args) {
        if (!args[0].equalsIgnoreCase(childCommand))
            return Result.invalidBecauseNull();
        return Result.valid(new BlobChildCommand(args, 0));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            return command.apply(sender, args);
        } catch (Exception exception) {
            Bukkit.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (aliases.contains(command.getName().toLowerCase())) {
            return tabCompleter.apply(sender, args);
        }
        return null;
    }
}
