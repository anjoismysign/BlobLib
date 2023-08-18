package us.mytheria.bloblib.command;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.PluginUpdater;
import us.mytheria.bloblib.entities.message.BlobMessage;
import us.mytheria.bloblib.managers.BlobPlugin;

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
                main.getMessageManager().getMessage("System.No-Permission").toCommandSender(sender);
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
                    main.getMessageManager().getMessage("System.Reload").toCommandSender(sender);
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
                    BlobMessage message = BlobLibAssetAPI.getMessage("BlobLib.Updater-Successful");
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
                        BlobLibAssetAPI.getMessage("BlobLib.Download-Usage")
                                .toCommandSender(sender);
                        return true;
                    } else if (length < 3) {
                        BlobLibAssetAPI.getMessage("BlobLib.Download-GitHub-Usage")
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
                            BlobLibAssetAPI.getMessage("BlobLib.Download-GitHub-Usage")
                                    .toCommandSender(sender);
                            return true;
                        }
                        String owner = split[0];
                        String repo = split[1];
                        RepositoryDownload download = downloadGitHub(owner, repo);
                        boolean successful = download.successful();
                        if (successful)
                            BlobLibAssetAPI.getMessage("BlobLib.Download-GitHub-Successful")
                                    .modder()
                                    .replace("%randomColor%", main.getColorManager().randomColor().toString())
                                    .replace("%fileName%", download.fileName())
                                    .get()
                                    .toCommandSender(sender);
                        else {
                            DownloadError error = download.error();
                            switch (error) {
                                case NO_CONNECTION -> BlobLibAssetAPI.getMessage("BlobLib.No-Connection")
                                        .toCommandSender(sender);
                                case REPO_NOT_FOUND -> BlobLibAssetAPI.getMessage("BlobLib.Repository-Not-Found")
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
                List<String> l = new ArrayList<>();
                int length = args.length;
                switch (length) {
                    case 1 -> {
                        l.add("reload");
                        l.add("update");
                        l.add("download");
                    }
                    case 2 -> {
                        String arg = args[0].toLowerCase();
                        switch (arg) {
                            case "update" -> {
                                l.addAll(main.getPluginManager().values().stream()
                                        .map(BlobPlugin::getPluginUpdater)
                                        .filter(Objects::nonNull)
                                        .filter(PluginUpdater::hasAvailableUpdate)
                                        .map(PluginUpdater::getPlugin)
                                        .map(PluginBase::getName)
                                        .toList());
                            }
                            case "download" -> {
                                l.add("GitHub");
                            }
                            default -> {
                            }
                        }
                    }
                    default -> {
                    }
                }
                return l;
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
