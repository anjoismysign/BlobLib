package io.github.anjoismysign.bloblib.command;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.api.BlobLibInventoryAPI;
import io.github.anjoismysign.bloblib.api.BlobLibMessageAPI;
import io.github.anjoismysign.bloblib.api.BlobLibSoundAPI;
import io.github.anjoismysign.bloblib.component.textbubble.TextBubbleComponent;
import io.github.anjoismysign.bloblib.entities.PluginUpdater;
import io.github.anjoismysign.bloblib.entities.area.AreaIO;
import io.github.anjoismysign.bloblib.entities.inventory.BlobInventoryTracker;
import io.github.anjoismysign.bloblib.entities.message.BlobMessage;
import io.github.anjoismysign.bloblib.entities.message.BlobSound;
import io.github.anjoismysign.bloblib.entities.positionable.Positionable;
import io.github.anjoismysign.bloblib.entities.positionable.PositionableIO;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableItem;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatablePositionable;
import io.github.anjoismysign.bloblib.itemapi.ItemMaterial;
import io.github.anjoismysign.bloblib.itemapi.ItemMaterialManager;
import io.github.anjoismysign.bloblib.managers.BlobPlugin;
import io.github.anjoismysign.bloblib.managers.PluginManager;
import io.github.anjoismysign.bloblib.utilities.PlayerUtil;
import io.github.anjoismysign.bloblib.utilities.TextColor;
import io.github.anjoismysign.skeramidcommands.command.Command;
import io.github.anjoismysign.skeramidcommands.command.CommandTarget;
import io.github.anjoismysign.skeramidcommands.commandtarget.BukkitCommandTarget;
import io.github.anjoismysign.skeramidcommands.commandtarget.CommandTargetBuilder;
import io.github.anjoismysign.skeramidcommands.server.bukkit.BukkitAdapter;
import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        teleport(bloblib);
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
        Command selfUpdate = bloblib.child("selfupdate");
        selfUpdate.onExecute(((permissionMessenger, args) -> {
            CommandSender sender = BukkitAdapter.getInstance().of(permissionMessenger);
            PluginUpdater updater = main.getBloblibupdater();

            BlobMessage message = BlobLibMessageAPI.getInstance()
                    .getMessage("BlobLib.Updater-Successful", sender);

            message.modder()
                    .replace("%randomColor%", main.getColorManager().randomColor().toString())
                    .replace("%plugin%", updater.getPlugin().getName())
                    .replace("%version%", updater.getLatestVersion())
                    .get()
                    .toCommandSender(sender);
        }));

        Command pluginUpdate = bloblib.child("update");
        PluginManager pluginManager = main.getPluginManager();
        CommandTarget<BlobPlugin> target = CommandTargetBuilder.fromMap(() -> pluginManager.getPluginsAsMap().entrySet().stream()
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


        pluginUpdate.setParameters(target);
        pluginUpdate.onExecute(((permissionMessenger, args) -> {
            CommandSender sender = BukkitAdapter.getInstance().of(permissionMessenger);
            PluginUpdater updater;
            BlobPlugin plugin = target.parse(args[0]);
            if (plugin != null && plugin.getPluginUpdater() != null) {
                updater = plugin.getPluginUpdater();
            } else {
                return;
            }
            boolean successful = updater.download();
            if (!successful)
                return;
            BlobMessage message = BlobLibMessageAPI.getInstance()
                    .getMessage("BlobLib.Updater-Successful", sender);

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
        }));
    }

    public void translatableitem(@NotNull Command bloblib) {
        Command command = bloblib.child("translatableitem");
        ItemMaterialManager materialManager = ItemMaterialManager.getInstance();
        CommandTarget<ItemMaterial> target = CommandTargetBuilder.fromMap(materialManager::getItems);
        Command get = command.child("get");
        get.setParameters(target);
        get.onExecute(((permissionMessenger, args) -> {
            CommandSender sender = BukkitAdapter.getInstance().of(permissionMessenger);
            if (args.length < 1) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("TranslatableItem.Usage", sender)
                        .toCommandSender(sender);
                return;
            }
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
        Command save = command.child("save");
        save.setParameters(CommandTargetBuilder.fromMap(() -> Map.of("Type the key you wanna use", "")));
        save.onExecute(((permissionMessenger, args) -> {
            CommandSender sender = BukkitAdapter.getInstance().of(permissionMessenger);
            if (!(sender instanceof Player player)) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("System.Console-Not-Allowed-Command", sender)
                        .toCommandSender(sender);
                return;
            }
            String key = args[0];
            if (key.isEmpty()) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("TranslatablePositionable.Key-Cannot-Be-Empty", player)
                        .handle(player);
                return;
            }
            Location location = player.getLocation();
            PositionableIO.INSTANCE.writeWithKey(location, key);
            BlobLibMessageAPI.getInstance()
                    .getMessage("TranslatablePositionable.Save", player)
                    .modder()
                    .replace("%reference%", key)
                    .get()
                    .handle(player);
        }));
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
            String key = PositionableIO.INSTANCE.writeRandom(location);
            BlobLibMessageAPI.getInstance()
                    .getMessage("TranslatablePositionable.Random", player)
                    .modder()
                    .replace("%reference%", key)
                    .get()
                    .handle(player);
        }));
        Command teleport = command.child("teleport");
        CommandTarget<Player> onlinePlayers = BukkitCommandTarget.ONLINE_PLAYERS();
        CommandTarget<TranslatablePositionable> target = CommandTargetBuilder.fromMap(() -> main.getTranslatablePositionableManager().getDefault());
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
                player.teleport(location);
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
        open.setParameters(CommandTargetBuilder.fromMap(() -> BlobLibInventoryAPI.getInstance()
                .getInventoryManager().getBlobInventories()), onlinePlayers);
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
        CommandTarget<BlobMessage> target = CommandTargetBuilder.fromMap(() -> BlobLibMessageAPI.getInstance().getDefault());
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
                sender.sendMessage("BlobMessage not found: '" + key + "'");
                return;
            }
            message.handle(player);
        }));
    }

    public void blobsound(@NotNull Command bloblib) {
        Command command = bloblib.child("blobsound");
        Command send = command.child("send");
        CommandTarget<BlobSound> target = CommandTargetBuilder.fromMap(() -> BlobLibSoundAPI.getInstance().getDefault());
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
            BlobSound sound = target.parse(key);
            if (sound == null) {
                sender.sendMessage("BlobSound not found: '" + key + "'");
                return;
            }
            sound.handle(player);
        }));
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
        } catch (MalformedURLException exception) {
            return RepositoryDownload.FAIL(DownloadError.MALFORMED_URL);
        }
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException exception) {
            return RepositoryDownload.FAIL(DownloadError.NO_CONNECTION);
        }
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException exception) {
            return RepositoryDownload.FAIL(DownloadError.PROTOCOL_ERROR);
        }
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException exception) {
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
        } catch (IOException exception) {
            return RepositoryDownload.FAIL(DownloadError.UNKNOWN);
        }
        Gson gson = new Gson();
        JsonArray releases = gson.fromJson(response.toString(), JsonArray.class);
        JsonObject latestRelease = releases.get(0).getAsJsonObject();
        String latestUrl = latestRelease.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
        String fileName = latestUrl.substring(latestUrl.lastIndexOf("/") + 1);
        try {
            url = new URL(latestUrl);
        } catch (MalformedURLException exception) {
            return RepositoryDownload.FAIL(DownloadError.MALFORMED_URL);
        }
        Path targetPath = Path.of("plugins", fileName);
        try (InputStream inputStream = url.openStream()) {
            Files.copy(inputStream, targetPath);
            return new RepositoryDownload(fileName, DownloadError.NONE);
        } catch (IOException exception) {
            return RepositoryDownload.FAIL(DownloadError.UNKNOWN);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public void teleport(@NotNull Command bloblib) {
        Command command = bloblib.child("teleport");
        var playerTarget = BukkitCommandTarget.ONLINE_PLAYERS();
        CommandTarget<Double> coordinateTarget = new CommandTarget<Double>() {
            @Override
            public List<String> get() {
                return List.of("0.0");
            }

            @Override
            public @Nullable Double parse(String s) {
                double value;
                try {
                    value = Double.parseDouble(s);
                    return value;
                } catch (NumberFormatException exception) {
                    return null;
                }
            }
        };
        command.setParameters(coordinateTarget, coordinateTarget, coordinateTarget, playerTarget);
        command.onExecute(((permissionMessenger, args) -> {
            int length = args.length;
            CommandSender sender = BukkitAdapter.getInstance().of(permissionMessenger);
            Player player;

            if (length < 4) {
                if (!(sender instanceof Player)) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("System.Console-Not-Allowed-Command", sender)
                            .toCommandSender(sender);
                    return;
                }
                player = (Player) sender;
            } else {
                player = playerTarget.parse(args[3]);
                if (player == null) {
                    BlobLibMessageAPI.getInstance()
                            .getMessage("Player.Not-Found", sender)
                            .toCommandSender(sender);
                    return;
                }
            }

            double x, y, z;
            x = coordinateTarget.parse(args[0]);
            y = coordinateTarget.parse(args[1]);
            z = coordinateTarget.parse(args[2]);

            Location playerLocation = player.getLocation();
            player.teleport(new Location(player.getWorld(), x, y, z, playerLocation.getYaw(), playerLocation.getPitch()));
        }));
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

    private record RepositoryDownload(@Nullable String fileName,
                                      @NotNull BlobLibCommand.DownloadError error) {
        private static RepositoryDownload FAIL(DownloadError error) {
            return new RepositoryDownload(null, error);
        }

        public boolean successful() {
            return error == DownloadError.NONE;
        }
    }
}
