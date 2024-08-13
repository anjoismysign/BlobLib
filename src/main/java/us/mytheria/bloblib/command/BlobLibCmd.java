package us.mytheria.bloblib.command;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.api.BlobLibInventoryAPI;
import us.mytheria.bloblib.api.BlobLibMessageAPI;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.entities.PluginUpdater;
import us.mytheria.bloblib.entities.inventory.BlobInventoryTracker;
import us.mytheria.bloblib.entities.message.BlobMessage;
import us.mytheria.bloblib.entities.positionable.Positionable;
import us.mytheria.bloblib.entities.translatable.TranslatableItem;
import us.mytheria.bloblib.entities.translatable.TranslatablePositionable;
import us.mytheria.bloblib.managers.BlobPlugin;
import us.mytheria.bloblib.utilities.PlayerUtil;
import us.mytheria.bloblib.utilities.TextColor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        try {
            if (!sender.hasPermission("bloblib.admin")) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("System.No-Permission", sender)
                        .toCommandSender(sender);
                return true;
            }
            int length = args.length;
            if (length < 1) {
                debug(sender);
                return true;
            }
            String arg1 = args[0].toLowerCase();
            switch (arg1) {
                case "reload" -> {
                    main.reload();
                    BlobLibMessageAPI.getInstance()
                            .getMessage("System.Reload", sender)
                            .toCommandSender(sender);
                    return true;
                }
                case "blobmessage" -> {
                    if (length < 3) {
                        BlobLibMessageAPI.getInstance()
                                .getMessage("BlobMessage.Usage", sender)
                                .toCommandSender(sender);
                        return true;
                    }
                    String arg2 = args[1].toLowerCase();
                    if (!arg2.equals("send")) {
                        BlobLibMessageAPI.getInstance()
                                .getMessage("BlobMessage.Usage", sender)
                                .toCommandSender(sender);
                        return true;
                    }
                    String key = args[2];
                    Player player;
                    if (length < 4) {
                        if (!(sender instanceof Player)) {
                            BlobLibMessageAPI.getInstance()
                                    .getMessage("System.Console-Not-Allowed-Command", sender)
                                    .toCommandSender(sender);
                            return true;
                        }
                        player = (Player) sender;
                    } else {
                        String playerName = args[3];
                        player = Bukkit.getPlayer(playerName);
                        if (player == null) {
                            BlobLibMessageAPI.getInstance()
                                    .getMessage("Player.Not-Found", sender)
                                    .toCommandSender(sender);
                            return true;
                        }
                    }
                    BlobMessage message = BlobLibMessageAPI.getInstance()
                            .getMessage(key, player);
                    if (message == null) {
                        BlobLibMessageAPI.getInstance()
                                .getMessage("BlobMessage.Not-Found", sender)
                                .modder()
                                .replace("%key%", key)
                                .get()
                                .toCommandSender(sender);
                        return true;
                    }
                    message.handle(player);
                    return true;
                }
                case "closeinventory" -> {
                    if (length < 2) {
                        BlobLibMessageAPI.getInstance()
                                .getMessage("CloseInventory.Usage", sender)
                                .toCommandSender(sender);
                        return true;
                    }
                    String playerName = args[1];
                    Player player = Bukkit.getPlayer(playerName);
                    if (player == null) {
                        BlobLibMessageAPI.getInstance()
                                .getMessage("Player.Not-Found", sender)
                                .toCommandSender(sender);
                        return true;
                    }
                    player.closeInventory();
                    return true;
                }
                case "blobinventory" -> {
                    if (length < 3) {
                        BlobLibMessageAPI.getInstance()
                                .getMessage("BlobInventory.Usage", sender)
                                .toCommandSender(sender);
                        return true;
                    }
                    String arg2 = args[1].toLowerCase();
                    if (!arg2.equals("open")) {
                        BlobLibMessageAPI.getInstance()
                                .getMessage("BlobInventory.Usage", sender)
                                .toCommandSender(sender);
                        return true;
                    }
                    String key = args[2];
                    Player player;
                    if (length < 4) {
                        if (!(sender instanceof Player)) {
                            BlobLibMessageAPI.getInstance()
                                    .getMessage("System.Console-Not-Allowed-Command", sender)
                                    .toCommandSender(sender);
                            return true;
                        }
                        player = (Player) sender;
                    } else {
                        String playerName = args[3];
                        player = Bukkit.getPlayer(playerName);
                        if (player == null) {
                            BlobLibMessageAPI.getInstance()
                                    .getMessage("Player.Not-Found", sender)
                                    .toCommandSender(sender);
                            return true;
                        }
                    }
                    BlobInventoryTracker tracker = BlobLibInventoryAPI.getInstance()
                            .trackInventory(player, key);
                    if (tracker == null) {
                        BlobLibMessageAPI.getInstance()
                                .getMessage("BlobInventory.Not-Found", sender)
                                .modder()
                                .replace("%key%", key)
                                .get()
                                .toCommandSender(sender);
                        return true;
                    }
                    tracker.getInventory().open(player);
                    return true;
                }
                case "translatableitem" -> {
                    if (length < 3) {
                        BlobLibMessageAPI.getInstance()
                                .getMessage("TranslatableItem.Usage", sender)
                                .toCommandSender(sender);
                        return true;
                    }
                    String arg2 = args[1].toLowerCase();
                    String key = args[2];
                    switch (arg2) {
                        case "get" -> {
                            if (!(sender instanceof Player player)) {
                                BlobLibMessageAPI.getInstance()
                                        .getMessage("System.Console-Not-Allowed-Command", sender)
                                        .toCommandSender(sender);
                                return true;
                            }
                            TranslatableItem item = TranslatableItem.by(key);
                            if (item == null) {
                                BlobLibMessageAPI.getInstance()
                                        .getMessage("TranslatableItem.Not-Found", player)
                                        .modder()
                                        .replace("%key%", key)
                                        .get()
                                        .handle(player);
                                return true;
                            }
                            ItemStack clone = item.localize(player).getClone();
                            PlayerUtil.giveItemToInventoryOrDrop(player, clone);
                            return true;
                        }
                        case "give" -> {
                            if (length < 4) {
                                BlobLibMessageAPI.getInstance()
                                        .getMessage("TranslatableItem.Usage", sender)
                                        .toCommandSender(sender);
                                return true;
                            }
                            String playerName = args[3];
                            Player player = main.getServer().getPlayer(playerName);
                            if (player == null) {
                                BlobLibMessageAPI.getInstance()
                                        .getMessage("Player.Not-Found", sender)
                                        .toCommandSender(sender);
                                return true;
                            }
                            TranslatableItem item = TranslatableItem.by(key);
                            if (item == null) {
                                BlobLibMessageAPI.getInstance()
                                        .getMessage("TranslatableItem.Not-Found", sender)
                                        .modder()
                                        .replace("%key%", key)
                                        .get()
                                        .toCommandSender(sender);
                                return true;
                            }
                            ItemStack clone = item.localize(player).getClone();
                            PlayerUtil.giveItemToInventoryOrDrop(player, clone);
                            return true;
                        }
                        default -> {
                            BlobLibMessageAPI.getInstance()
                                    .getMessage("TranslatableItem.Usage", sender)
                                    .toCommandSender(sender);
                            return true;
                        }
                    }
                }
                case "translatablepositionable" -> {
                    if (length < 3) {
                        sender.sendMessage(TextColor.PARSE("&c/bloblib translatablepositionable open <player>"));
                        return true;
                    }
                    String arg2 = args[1].toLowerCase();
                    if (!arg2.equals("teleport")) {
                        sender.sendMessage(TextColor.PARSE("&c/bloblib translatablepositionable open <player>"));
                        return true;
                    }
                    String key = args[2];
                    Player player;
                    if (length < 4) {
                        if (!(sender instanceof Player)) {
                            BlobLibMessageAPI.getInstance()
                                    .getMessage("System.Console-Not-Allowed-Command", sender)
                                    .toCommandSender(sender);
                            return true;
                        }
                        player = (Player) sender;
                    } else {
                        String playerName = args[3];
                        player = Bukkit.getPlayer(playerName);
                        if (player == null) {
                            BlobLibMessageAPI.getInstance()
                                    .getMessage("Player.Not-Found", sender)
                                    .toCommandSender(sender);
                            return true;
                        }
                    }
                    TranslatablePositionable translatablePositionable = TranslatablePositionable.by(key);
                    if (translatablePositionable == null) {
                        sender.sendMessage(TextColor.PARSE("&cNot found: " + key));
                        return true;
                    }
                    Positionable positionable = translatablePositionable.get();
                    Location location = positionable.getPositionableType().isLocatable() ? positionable.toLocation() : positionable.toLocation(player.getWorld());
                    player.teleport(location);
                    return true;
                }
                case "update" -> {
                    PluginUpdater updater;
                    boolean isPlugin = false;
                    if (length > 1) {
                        String input = args[1];
                        BlobPlugin plugin = main.getPluginManager().get(input);
                        if (plugin != null && plugin.getPluginUpdater() != null) {
                            updater = plugin.getPluginUpdater();
                            isPlugin = true;
                        } else
                            updater = main.getBloblibupdater();
                    } else
                        updater = main.getBloblibupdater();
                    boolean successful = updater.download();
                    if (!successful)
                        return true;
                    BlobMessage message = BlobLibMessageAPI.getInstance()
                            .getMessage("BlobLib.Updater-Successful", sender);
                    if (isPlugin) {
                        message.modder()
                                .replace("%randomColor%", main.getColorManager().randomColor().toString())
                                .replace("%plugin%", updater.getPlugin().getName())
                                .replace("%version%", updater.getLatestVersion())
                                .get()
                                .toCommandSender(sender);
                    } else
                        message.modder()
                                .replace("%randomColor%", main.getColorManager().randomColor().toString())
                                .replace("%plugin%", updater.getPlugin().getName())
                                .replace("%version%", updater.getLatestVersion())
                                .get()
                                .toCommandSender(sender);
                    return true;
                }
                case "download" -> {
                    if (length < 2) {
                        BlobLibMessageAPI.getInstance()
                                .getMessage("BlobLib.Download-Usage", sender)
                                .toCommandSender(sender);
                        return true;
                    } else if (length < 3) {
                        BlobLibMessageAPI.getInstance()
                                .getMessage("BlobLib.Download-GitHub-Usage", sender)
                                .toCommandSender(sender);
                        return true;
                    } else {
                        String provider = args[1];
                        if (!provider.equalsIgnoreCase("GitHub")) {
                            sender.sendMessage(ChatColor.RED + "Currently supported providers: [ GitHub ]");
                            return true;
                        }
                        String input = args[2];
                        String[] split = input.split("/");
                        if (split.length != 2) {
                            BlobLibMessageAPI.getInstance()
                                    .getMessage("BlobLib.Download-GitHub-Usage", sender)
                                    .toCommandSender(sender);
                            return true;
                        }
                        String owner = split[0];
                        String repo = split[1];
                        RepositoryDownload download = downloadGitHub(owner, repo);
                        boolean successful = download.successful();
                        if (successful)
                            BlobLibMessageAPI.getInstance()
                                    .getMessage("BlobLib.Download-GitHub-Successful", sender)
                                    .modder()
                                    .replace("%randomColor%", main.getColorManager().randomColor().toString())
                                    .replace("%fileName%", download.fileName())
                                    .get()
                                    .toCommandSender(sender);
                        else {
                            DownloadError error = download.error();
                            switch (error) {
                                case NO_CONNECTION -> BlobLibMessageAPI.getInstance()
                                        .getMessage("BlobLib.No-Connection", sender)
                                        .toCommandSender(sender);
                                case REPO_NOT_FOUND -> BlobLibMessageAPI.getInstance()
                                        .getMessage("BlobLib.Repository-Not-Found", sender)
                                        .toCommandSender(sender);
                                default -> sender.sendMessage(ChatColor.RED + "Could not download file");
                            }
                        }
                        return true;
                    }
                }
                default -> {
                    debug(sender);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Will send debug information to CommandSender
     *
     * @param sender The CommandSender
     */
    public void debug(CommandSender sender) {
        if (sender.hasPermission("bloblib.debug")) {
            sender.sendMessage("/bloblib reload");
            sender.sendMessage("/bloblib update");
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
                List<String> list = new ArrayList<>();
                int length = args.length;
                switch (length) {
                    case 1 -> {
                        list.add("reload");
                        list.add("update");
                        list.add("download");
                        list.add("translatableitem");
                        list.add("translatablepositionable");
                        list.add("blobinventory");
                        list.add("closeinventory");
                        list.add("blobmessage");
                    }
                    case 2 -> {
                        String arg = args[0].toLowerCase();
                        switch (arg) {
                            case "update" -> {
                                list.addAll(main.getPluginManager().values().stream()
                                        .map(BlobPlugin::getPluginUpdater)
                                        .filter(Objects::nonNull)
                                        .filter(PluginUpdater::hasAvailableUpdate)
                                        .map(PluginUpdater::getPlugin)
                                        .map(PluginBase::getName)
                                        .toList());
                            }
                            case "download" -> {
                                list.add("GitHub");
                            }
                            case "translatableitem" -> {
                                list.add("get");
                                list.add("give");
                            }
                            case "blobinventory" -> {
                                list.add("open");
                            }
                            case "translatablepositionable" -> {
                                list.add("teleport");
                            }
                            case "blobmessage" -> {
                                list.add("send");
                            }
                            case "closeinventory" -> {
                                list.addAll(main.getServer().getOnlinePlayers().stream()
                                        .map(Player::getName)
                                        .toList());
                            }
                            default -> {
                            }
                        }
                    }
                    case 3 -> {
                        String arg = args[0].toLowerCase();
                        switch (arg) {
                            case "translatableitem" -> {
                                String sub = args[1].toLowerCase();
                                if (sub.equals("get") || sub.equals("give")) {
                                    BlobLibTranslatableAPI.getInstance()
                                            .getTranslatableItems("en_us")
                                            .stream()
                                            .map(TranslatableItem::getReference)
                                            .forEach(list::add);
                                    return list;
                                }
                            }
                            case "blobinventory" -> {
                                String sub = args[1].toLowerCase();
                                if (sub.equals("open")) {
                                    BlobLibInventoryAPI.getInstance()
                                            .getBlobInventories().keySet()
                                            .forEach(list::add);
                                    return list;
                                }
                            }
                            case "translatablepositionable" -> {
                                String sub = args[1].toLowerCase();
                                if (sub.equals("teleport")) {
                                    return BlobLibTranslatableAPI.getInstance()
                                            .getTranslatablePositionables("en_us")
                                            .stream()
                                            .map(TranslatablePositionable::getReference)
                                            .toList();
                                }
                            }
                            case "blobmessage" -> {
                                String sub = args[1].toLowerCase();
                                if (sub.equals("send")) {
                                    BlobLibMessageAPI.getInstance()
                                            .getDefaultReferences()
                                            .forEach(list::add);
                                    return list;
                                }
                            }
                        }
                    }
                    case 4 -> {
                        String arg = args[0].toLowerCase();
                        switch (arg) {
                            case "translatableitem" -> {
                                String sub = args[1].toLowerCase();
                                if (sub.equals("give")) {
                                    list.addAll(main.getServer().getOnlinePlayers().stream()
                                            .map(Player::getName)
                                            .toList());
                                    return list;
                                }
                            }
                            case "blobinventory" -> {
                                String sub = args[1].toLowerCase();
                                if (sub.equals("open")) {
                                    list.addAll(main.getServer().getOnlinePlayers().stream()
                                            .map(Player::getName)
                                            .toList());
                                    return list;
                                }
                            }
                            case "translatablepositionable" -> {
                                String sub = args[1].toLowerCase();
                                if (sub.equals("teleport")) {
                                    list.addAll(main.getServer().getOnlinePlayers().stream()
                                            .map(Player::getName)
                                            .toList());
                                    return list;
                                }
                            }
                            case "blobmessage" -> {
                                String sub = args[1].toLowerCase();
                                if (sub.equals("send")) {
                                    list.addAll(main.getServer().getOnlinePlayers().stream()
                                            .map(Player::getName)
                                            .toList());
                                    return list;
                                }
                            }
                        }
                    }
                    default -> {
                    }
                }
                return list;
            }
        }
        return null;
    }

    private record RepositoryDownload(@Nullable String fileName,
                                      @NotNull DownloadError error) {
        private static RepositoryDownload FAIL(DownloadError error) {
            return new RepositoryDownload(null, error);
        }

        public boolean successful() {
            return error == DownloadError.NONE;
        }
    }

    private enum DownloadError {
        NO_CONNECTION,
        REPO_NOT_FOUND,
        NO_PERMISSION,
        MALFORMED_URL,
        PROTOCOL_ERROR,
        NONE,
        UNKNOWN
    }

    /**
     * Downloads a plugin from GitHub. It's expected that
     * file does not exist in the plugins' folder.
     *
     * @param owner The owner of the repo
     * @param repo  The repo name
     * @return the GitHubDownload object, null if unsuccessful
     */
    @NotNull
    private BlobLibCmd.RepositoryDownload downloadGitHub(String owner, String repo) {
        String repoUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/releases";
        URL url;
        try {
            url = new URL(repoUrl);
        } catch (MalformedURLException e) {
            return RepositoryDownload.FAIL(DownloadError.MALFORMED_URL);
        }
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            return RepositoryDownload.FAIL(DownloadError.NO_CONNECTION);
        }
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            return RepositoryDownload.FAIL(DownloadError.PROTOCOL_ERROR);
        }
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            BlobLib.getAnjoLogger().singleError("Repo not found: " + repoUrl);
            return RepositoryDownload.FAIL(DownloadError.REPO_NOT_FOUND);
        }
        StringBuilder response = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
        } catch (IOException e) {
            return RepositoryDownload.FAIL(DownloadError.UNKNOWN);
        }
        Gson gson = new Gson();
        JsonArray releases = gson.fromJson(response.toString(), JsonArray.class);
        JsonObject latestRelease = releases.get(0).getAsJsonObject();
        String latestUrl = latestRelease.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
        String fileName = latestUrl.substring(latestUrl.lastIndexOf("/") + 1);
        try {
            url = new URL(latestUrl);
        } catch (MalformedURLException e) {
            return RepositoryDownload.FAIL(DownloadError.MALFORMED_URL);
        }
        Path targetPath = Path.of("plugins", fileName);
        try (InputStream inputStream = url.openStream()) {
            Files.copy(inputStream, targetPath);
            return new RepositoryDownload(fileName, DownloadError.NONE);
        } catch (IOException e) {
            return RepositoryDownload.FAIL(DownloadError.UNKNOWN);
        }
    }
}
