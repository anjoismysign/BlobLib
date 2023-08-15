package us.mytheria.bloblib;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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

public class BlobLibUpdater {
    private final BlobLib plugin;
    private final String currentVersion;
    private boolean updateAvailable;
    private final UpdaterListener listener;
    private String latestVersion;

    protected BlobLibUpdater(BlobLib plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
        this.listener = new UpdaterListener(plugin);
        reload();
    }

    /**
     * Will reload the updater and re-run their checks
     */
    public void reload() {
        updateAvailable = !isLatestVersion();
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

    /**
     * Will get last known latest version of BlobLib
     *
     * @return the latest version of BlobLib
     */
    public String getLatestVersion() {
        return latestVersion;
    }

    private boolean isLatestVersion() {
        latestVersion = fetchLatestVersion();
        return currentVersion.equals(latestVersion);
    }

    private String fetchLatestVersion() {
        String latestURL = getLatestUrl();
        String[] split = latestURL.split("BlobLib-");
        String version = split[1];
        version = version.replace(".jar", "");
        return version;
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
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Path targetPath = Path.of("plugins", "BlobLib-" + latestVersion + ".jar");
        try (InputStream inputStream = url.openStream()) {
            Files.copy(inputStream, targetPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getLatestUrl() {
        String repoUrl = "https://api.github.com/repos/anjoismysign/BlobLib/releases";
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
        return latestRelease.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
    }

    private class UpdaterListener implements Listener {
        private final BlobLib plugin;

        public UpdaterListener(BlobLib plugin) {
            this.plugin = plugin;
        }

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
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                if (player == null || !player.isOnline())
                    return;
                BlobLibAssetAPI.getMessage("BlobLib.Update-Available")
                        .handle(player);
            }, 30L);
        }
    }
}
