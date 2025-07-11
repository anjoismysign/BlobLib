package io.github.anjoismysign.bloblib;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.anjoismysign.bloblib.api.BlobLibMessageAPI;
import io.github.anjoismysign.bloblib.entities.PluginUpdater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class BlobLibUpdater implements PluginUpdater {
    private final BlobLib plugin;
    private final String currentVersion;
    private final UpdaterListener listener;
    private boolean updateAvailable;
    private String latestVersion;

    protected BlobLibUpdater(BlobLib plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
        this.listener = new UpdaterListener(plugin, this);
        reload();
    }

    /**
     * Will reload the updater and re-run their checks
     */
    public void reload() {
        getLatestUrl();
        updateAvailable = latestVersion != null && !isLatestVersion();
        listener.reload(updateAvailable);
    }

    /**
     * Will return true if there is an update available
     *
     * @return true if there is an update available
     */
    public boolean hasAvailableUpdate() {
        return updateAvailable;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    /**
     * Will attempt to download latest version of BlobLib
     *
     * @return true if the download was successful
     */
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
        Path existentPath = Path.of("plugins", "BlobLib-" + currentVersion + ".jar");
        try {
            Files.deleteIfExists(existentPath);
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
        Path targetPath = Path.of("plugins", "BlobLib-" + latestVersion + ".jar");
        try (InputStream inputStream = url.openStream()) {
            Files.copy(inputStream, targetPath);
            return true;
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    private boolean isLatestVersion() {
        return currentVersion.equals(latestVersion);
    }

    private String getLatestUrl() {
        String repoUrl = "https://api.github.com/repos/anjoismysign/BlobLib/releases";
        URL url;
        try {
            url = new URL(repoUrl);
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
            return null;
        }
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            plugin.getLogger().severe("Could not connect to GitHub to check for updates");
            return null;
        }
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException exception) {
            exception.printStackTrace();
            return null;
        }
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (UnknownHostException ignored) {
            return null;
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
        StringBuilder response = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
        } catch (IOException exception) {
            exception.printStackTrace();
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

    private record UpdaterListener(BlobLib plugin, PluginUpdater updater) implements Listener {

        private void reload(boolean updateAvailable) {
            HandlerList.unregisterAll(this);
            if (updateAvailable)
                Bukkit.getPluginManager().registerEvents(this, plugin);
        }

        @EventHandler
        public void handle(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            if (!player.hasPermission("bloblib.admin"))
                return;
            UUID uuid = player.getUniqueId();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player != Bukkit.getPlayer(uuid))
                    return;
                BlobLibMessageAPI.getInstance()
                        .getMessage("BlobLib.Updater-Available", player)
                        .modder()
                        .replace("%randomColor%", plugin.getColorManager().randomColor().toString())
                        .replace("update %plugin%", "update")
                        .replace("%plugin%", plugin.getName())
                        .replace("%version%", updater.getLatestVersion())
                        .get()
                        .handle(player);
            }, 30L);
        }
    }
}
