package us.mytheria.bloblib.entities;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.BlobLibAssetAPI;

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

public class GithubPluginUpdater implements PluginUpdater {
    private final JavaPlugin plugin;
    private final String currentVersion, pluginName, author, repository;
    private boolean updateAvailable;
    private final UpdaterListener listener;
    private String latestVersion;

    public GithubPluginUpdater(JavaPlugin plugin,
                               String author, String repository) {
        this.plugin = plugin;
        this.author = author;
        this.repository = repository;
        PluginDescriptionFile description = plugin.getDescription();
        this.pluginName = description.getName();
        this.currentVersion = description.getVersion();
        this.listener = new UpdaterListener(plugin, pluginName, this);
        reload();
    }

    public void reload() {
        updateAvailable = !isLatestVersion();
        listener.reload(updateAvailable);
    }

    public boolean hasAvailableUpdate() {
        return updateAvailable;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    private boolean isLatestVersion() {
        return currentVersion.equals(latestVersion);
    }

    public boolean download() {
        if (!updateAvailable)
            return false;
        URL url;
        try {
            url = new URL(getLatestUrl());
        } catch (MalformedURLException e) {
            BlobLib.getAnjoLogger().error("Could not download latest version of BlobLib because " +
                    "the URL was malformed");
            return false;
        }
        Path existentPath = Path.of("plugins", pluginName + "-" + currentVersion + ".jar");
        try {
            Files.deleteIfExists(existentPath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Path targetPath = Path.of("plugins", pluginName + "-" + latestVersion + ".jar");
        try (InputStream inputStream = url.openStream()) {
            Files.copy(inputStream, targetPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    private String getLatestUrl() {
        String repoUrl = "https://api.github.com/repos/" + author + "/" + repository +
                "/releases";
        URL url;
        try {
            url = new URL(repoUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
            return null;
        }
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        StringBuilder response = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Gson gson = new Gson();
        JsonArray releases = gson.fromJson(response.toString(), JsonArray.class);
        JsonObject latestRelease = releases.get(0).getAsJsonObject();
        latestVersion = latestRelease.get("tag_name").getAsString();
        if (latestVersion.startsWith("v"))
            latestVersion = latestVersion.substring(1);
        return latestRelease.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
    }

    private record UpdaterListener(JavaPlugin plugin, String pluginName, PluginUpdater updater) implements Listener {
        private void reload(boolean updateAvailable) {
            HandlerList.unregisterAll(this);
            if (updateAvailable)
                Bukkit.getPluginManager().registerEvents(this, plugin);
        }

        @EventHandler
        public void handle(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            if (!player.hasPermission(pluginName.toLowerCase() + ".admin"))
                return;
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                if (player == null || !player.isOnline())
                    return;
                BlobLibAssetAPI.getMessage("BlobLib.Updater-Available")
                        .modder()
                        .replace("%randomColor%", BlobLib.getInstance().getColorManager().randomColor().toString())
                        .replace("%plugin%", plugin.getName())
                        .replace("%version%", updater.getLatestVersion())
                        .get()
                        .handle(player);
            }, 30L);
        }
    }
}
