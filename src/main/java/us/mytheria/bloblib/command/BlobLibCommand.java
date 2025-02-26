package us.mytheria.bloblib.command;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.papermc.paper.entity.TeleportFlag;
import me.anjoismysign.skeramidcommands.command.Command;
import me.anjoismysign.skeramidcommands.command.CommandTarget;
import me.anjoismysign.skeramidcommands.commandtarget.BukkitCommandTarget;
import me.anjoismysign.skeramidcommands.server.bukkit.BukkitAdapter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.api.BlobLibInventoryAPI;
import us.mytheria.bloblib.api.BlobLibMessageAPI;
import us.mytheria.bloblib.component.textbubble.TextBubbleComponent;
import us.mytheria.bloblib.entities.PluginUpdater;
import us.mytheria.bloblib.entities.area.AreaIO;
import us.mytheria.bloblib.entities.inventory.BlobInventoryTracker;
import us.mytheria.bloblib.entities.message.BlobMessage;
import us.mytheria.bloblib.entities.positionable.Positionable;
import us.mytheria.bloblib.entities.positionable.PositionableIO;
import us.mytheria.bloblib.entities.translatable.TranslatableItem;
import us.mytheria.bloblib.entities.translatable.TranslatablePositionable;
import us.mytheria.bloblib.itemapi.ItemMaterial;
import us.mytheria.bloblib.itemapi.ItemMaterialManager;
import us.mytheria.bloblib.managers.BlobPlugin;
import us.mytheria.bloblib.managers.PluginManager;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum BlobLibCommand {
    INSTANCE;

    private static final BlobLib main = BlobLib.getInstance();

    public void initialize() {
        Command bloblib = BukkitAdapter.getInstance().ofBukkitCommand(main.getName());
        reload(bloblib);
        update(bloblib);
        download(bloblib);
        translatableitem(bloblib);
        translatablepositionable(bloblib);
        translatablearea(bloblib);
        blobinventory(bloblib);
        closeinventory(bloblib);
        blobmessage(bloblib);
    }

    public void reload(@NotNull Command bloblib) {
        Command command = bloblib.child("reload");
        command.onExecute(((permissionMessenger, args) -> {
            CommandSender sender = BukkitAdapter.getInstance().of(permissionMessenger);
            main.reload();
            BlobLibMessageAPI.getInstance()
                    .getMessage("System.Reload", sender)
                    .toCommandSender(sender);
        }));
    }

    public void update(@NotNull Command bloblib) {
        Command command = bloblib.child("update");
        PluginManager pluginManager = main.getPluginManager();
        CommandTarget<BlobPlugin> target = BukkitCommandTarget.OF_MAP(() -> pluginManager.getPluginsAsMap().entrySet().stream()
                .filter(entry -> {
                    @Nullable PluginUpdater pluginUpdater = entry.getValue().getPluginUpdater();
                    if (pluginUpdater == null)
                        return false;
                    return pluginUpdater.hasAvailableUpdate();
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                )));
        command.setParameters(target);
        command.onExecute(((permissionMessenger, args) -> {
            CommandSender sender = BukkitAdapter.getInstance().of(permissionMessenger);
            int length = args.length;
            PluginUpdater updater;
            boolean isPlugin = false;
            if (length == 1) {
                BlobPlugin plugin = target.parse(args[0]);
                if (plugin != null && plugin.getPluginUpdater() != null) {
                    updater = plugin.getPluginUpdater();
                    isPlugin = true;
                } else {
                    return;
                }
            } else {
                updater = main.getBloblibupdater();
            }
            boolean successful = updater.download();
            if (!successful)
                return;
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
        }));
    }

    public void download(@NotNull Command bloblib) {
        Command command = bloblib.child("download");
        Command github = command.child("github");
        github.onExecute(((permissionMessenger, args) -> {
            CommandSender sender = BukkitAdapter.getInstance().of(permissionMessenger);
            String input = args[0];
            String[] split = input.split("/");
            if (split.length != 2) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("BlobLib.Download-GitHub-Usage", sender)
                        .toCommandSender(sender);
                return;
            }
            String owner = split[0];
            String repo = split[1];
            BlobLibCommand.RepositoryDownload download = downloadGitHub(owner, repo);
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
                BlobLibCommand.DownloadError error = download.error();
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
        }));

    }

    public void translatableitem(@NotNull Command bloblib) {
        Command command = bloblib.child("translatableitem");
        ItemMaterialManager materialManager = ItemMaterialManager.getInstance();
        CommandTarget<ItemMaterial> target = BukkitCommandTarget.OF_MAP(materialManager::getItems);
        Command get = command.child("get");
        get.setParameters(target);
        get.onExecute(((permissionMessenger, args) -> {
            CommandSender sender = BukkitAdapter.getInstance().of(permissionMessenger);
            String key = args[0];

            if (!(sender instanceof Player player)) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("System.Console-Not-Allowed-Command", sender)
                        .toCommandSender(sender);
                return;
            }
            TranslatableItem item = TranslatableItem.by(key);
            if (item == null) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("TranslatableItem.Not-Found", player)
                        .modder()
                        .replace("%key%", key)
                        .get()
                        .handle(player);
                return;
            }
            ItemStack clone = item.localize(player).getClone();
            PlayerUtil.giveItemToInventoryOrDrop(player, clone);
        }));
        Command give = command.child("give");
        CommandTarget<Player> onlinePlayers = BukkitCommandTarget.ONLINE_PLAYERS();
        give.setParameters(target, onlinePlayers);
        give.onExecute(((permissionMessenger, args) -> {
            CommandSender sender = BukkitAdapter.getInstance().of(permissionMessenger);
            String key = args[0];
            if (args.length == 1) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("TranslatableItem.Usage", sender)
                        .toCommandSender(sender);
                return;
            }
            Player player = onlinePlayers.parse(args[1]);
            if (player == null) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("Player.Not-Found", sender)
                        .toCommandSender(sender);
                return;
            }
            TranslatableItem item = TranslatableItem.by(key);
            if (item == null) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("TranslatableItem.Not-Found", sender)
                        .modder()
                        .replace("%key%", key)
                        .get()
                        .toCommandSender(sender);
                return;
            }
            ItemStack clone = item.localize(player).getClone();
            PlayerUtil.giveItemToInventoryOrDrop(player, clone);
        }));

    }

    public void translatablepositionable(@NotNull Command bloblib) {
        Command command = bloblib.child("translatablepositionable");
        Command random = command.child("random");
        random.onExecute(((permissionMessenger, args) -> {
            CommandSender sender = BukkitAdapter.getInstance().of(permissionMessenger);
            if (!(sender instanceof Player player)) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("System.Console-Not-Allowed-Command", sender)
                        .toCommandSender(sender);
                return;
            }
            Location location = player.getLocation();
            String reference = PositionableIO.INSTANCE.writeRandom(location);
            BlobLibMessageAPI.getInstance()
                    .getMessage("TranslatablePositionable.Random", player)
                    .modder()
                    .replace("%reference%", reference)
                    .get()
                    .handle(player);
        }));
        Command teleport = command.child("teleport");
        CommandTarget<Player> onlinePlayers = BukkitCommandTarget.ONLINE_PLAYERS();
        CommandTarget<TranslatablePositionable> target = BukkitCommandTarget.OF_MAP(() -> main.getTranslatablePositionableManager().getDefault());
        teleport.setParameters(target, onlinePlayers);
        teleport.onExecute(((permissionMessenger, args) -> {
            CommandSender sender = BukkitAdapter.getInstance().of(permissionMessenger);
            int length = args.length;

            Player player;
            if (length < 2) {
                if (!(sender instanceof Player)) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("System.Console-Not-Allowed-Command", sender)
                            .toCommandSender(sender);
                    return;
                }
                player = (Player) sender;
            } else {
                player = onlinePlayers.parse(args[1]);
                if (player == null) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("Player.Not-Found", sender)
                            .toCommandSender(sender);
                    return;
                }
            }
            String key = args[0];
            TranslatablePositionable translatablePositionable = target.parse(key);
            if (translatablePositionable == null) {
                sender.sendMessage(TextColor.PARSE("&cNot found: " + key));
                return;
            }
            Positionable positionable = translatablePositionable.get();
            Location location = positionable.getPositionableType().isLocatable() ? positionable.toLocation() : positionable.toLocation(player.getWorld());
            List<Entity> passengers = player.getPassengers();
            boolean isValid = passengers.size() == 1 && TextBubbleComponent.entityTracking.get(passengers.getFirst()) != null;
            if (isValid)
                player.teleport(location, TeleportFlag.EntityState.RETAIN_PASSENGERS);
            else
                player.teleport(location);
        }));
    }

    public void translatablearea(@NotNull Command bloblib) {
        Command command = bloblib.child("translatablearea");
        Command random = command.child("random");
        random.onExecute(((permissionMessenger, args) -> {
            CommandSender sender = BukkitAdapter.getInstance().of(permissionMessenger);
            if (!(sender instanceof Player player)) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("System.Console-Not-Allowed-Command", sender)
                        .toCommandSender(sender);
                return;
            }
            @Nullable String reference = AreaIO.INSTANCE.writeRandom(player);
            if (reference == null) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("TranslatableArea.Random-Fail", player)
                        .handle(player);
                return;
            }
            BlobLibMessageAPI.getInstance()
                    .getMessage("TranslatableArea.Random", player)
                    .modder()
                    .replace("%reference%", reference)
                    .get()
                    .handle(player);
        }));
    }

    public void blobinventory(@NotNull Command bloblib) {
        Command command = bloblib.child("blobinventory");
        Command open = command.child("open");
        CommandTarget<Player> onlinePlayers = BukkitCommandTarget.ONLINE_PLAYERS();
        open.setParameters(BukkitCommandTarget.OF_MAP(() -> BlobLibInventoryAPI.getInstance()
                .getBlobInventories()), onlinePlayers);
        open.onExecute(((permissionMessenger, args) -> {
            CommandSender sender = BukkitAdapter.getInstance().of(permissionMessenger);
            int length = args.length;

            String key = args[0];
            Player player;
            if (length < 2) {
                if (!(sender instanceof Player)) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("System.Console-Not-Allowed-Command", sender)
                            .toCommandSender(sender);
                    return;
                }
                player = (Player) sender;
            } else {
                player = onlinePlayers.parse(args[1]);
                if (player == null) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("Player.Not-Found", sender)
                            .toCommandSender(sender);
                    return;
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
                return;
            }
            tracker.getInventory().open(player);
        }));
    }

    public void closeinventory(@NotNull Command bloblib) {
        Command command = bloblib.child("closeinventory");
        CommandTarget<Player> onlinePlayers = BukkitCommandTarget.ONLINE_PLAYERS();
        command.setParameters(onlinePlayers);
        command.onExecute(((permissionMessenger, args) -> {
            CommandSender sender = BukkitAdapter.getInstance().of(permissionMessenger);
            Player player = onlinePlayers.parse(args[0]);
            if (player == null) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("Player.Not-Found", sender)
                        .toCommandSender(sender);
                return;
            }
            player.closeInventory();
        }));
    }

    public void blobmessage(@NotNull Command bloblib) {
        Command command = bloblib.child("blobmessage");
        Command send = command.child("send");
        CommandTarget<BlobMessage> target = BukkitCommandTarget.OF_MAP(() -> BlobLibMessageAPI.getInstance().getDefault());
        CommandTarget<Player> onlinePlayers = BukkitCommandTarget.ONLINE_PLAYERS();
        send.setParameters(target, onlinePlayers);
        send.onExecute(((permissionMessenger, args) -> {
            CommandSender sender = BukkitAdapter.getInstance().of(permissionMessenger);
            int length = args.length;

            String key = args[0];
            Player player;
            if (length < 2) {
                if (!(sender instanceof Player)) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("System.Console-Not-Allowed-Command", sender)
                            .toCommandSender(sender);
                    return;
                }
                player = (Player) sender;
            } else {
                player = onlinePlayers.parse(args[1]);
                if (player == null) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("Player.Not-Found", sender)
                            .toCommandSender(sender);
                    return;
                }
            }
            BlobMessage message = target.parse(key);
            if (message == null) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("BlobMessage.Not-Found", sender)
                        .modder()
                        .replace("%key%", key)
                        .get()
                        .toCommandSender(sender);
                return;
            }
            message.handle(player);
        }));
    }

    private record RepositoryDownload(@Nullable String fileName,
                                      @NotNull BlobLibCommand.DownloadError error) {
        private static BlobLibCommand.RepositoryDownload FAIL(BlobLibCommand.DownloadError error) {
            return new BlobLibCommand.RepositoryDownload(null, error);
        }

        public boolean successful() {
            return error == BlobLibCommand.DownloadError.NONE;
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
    private BlobLibCommand.RepositoryDownload downloadGitHub(String owner, String repo) {
        String repoUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/releases";
        URL url;
        try {
            url = new URL(repoUrl);
        } catch ( MalformedURLException e ) {
            return BlobLibCommand.RepositoryDownload.FAIL(BlobLibCommand.DownloadError.MALFORMED_URL);
        }
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch ( IOException e ) {
            return BlobLibCommand.RepositoryDownload.FAIL(BlobLibCommand.DownloadError.NO_CONNECTION);
        }
        try {
            connection.setRequestMethod("GET");
        } catch ( ProtocolException e ) {
            return BlobLibCommand.RepositoryDownload.FAIL(BlobLibCommand.DownloadError.PROTOCOL_ERROR);
        }
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch ( IOException e ) {
            BlobLib.getAnjoLogger().singleError("Repo not found: " + repoUrl);
            return BlobLibCommand.RepositoryDownload.FAIL(BlobLibCommand.DownloadError.REPO_NOT_FOUND);
        }
        StringBuilder response = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
        } catch ( IOException e ) {
            return BlobLibCommand.RepositoryDownload.FAIL(BlobLibCommand.DownloadError.UNKNOWN);
        }
        Gson gson = new Gson();
        JsonArray releases = gson.fromJson(response.toString(), JsonArray.class);
        JsonObject latestRelease = releases.get(0).getAsJsonObject();
        String latestUrl = latestRelease.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
        String fileName = latestUrl.substring(latestUrl.lastIndexOf("/") + 1);
        try {
            url = new URL(latestUrl);
        } catch ( MalformedURLException e ) {
            return BlobLibCommand.RepositoryDownload.FAIL(BlobLibCommand.DownloadError.MALFORMED_URL);
        }
        Path targetPath = Path.of("plugins", fileName);
        try (InputStream inputStream = url.openStream()) {
            Files.copy(inputStream, targetPath);
            return new BlobLibCommand.RepositoryDownload(fileName, BlobLibCommand.DownloadError.NONE);
        } catch ( IOException e ) {
            return BlobLibCommand.RepositoryDownload.FAIL(BlobLibCommand.DownloadError.UNKNOWN);
        }
    }
}
