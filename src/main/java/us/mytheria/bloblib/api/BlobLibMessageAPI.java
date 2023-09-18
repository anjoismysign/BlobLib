package us.mytheria.bloblib.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.message.ReferenceBlobMessage;
import us.mytheria.bloblib.managers.MessageManager;

import java.io.File;

public class BlobLibMessageAPI {
    private static BlobLibMessageAPI instance;
    private final BlobLib plugin;

    private BlobLibMessageAPI(BlobLib plugin) {
        this.plugin = plugin;
    }

    public static BlobLibMessageAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibMessageAPI.instance = new BlobLibMessageAPI(plugin);
        }
        return instance;
    }

    public static BlobLibMessageAPI getInstance() {
        return getInstance(null);
    }

    /**
     * @return The message manager
     */
    public MessageManager getMessageManager() {
        return plugin.getMessageManager();
    }

    /**
     * @return The messages file
     */
    @NotNull
    public File getMessagesDirectory() {
        return plugin.getFileManager().messagesDirectory();
    }

    /**
     * @return The messages file path
     */
    @NotNull
    public String getMessagesFilePath() {
        return plugin.getFileManager().messagesDirectory().getPath();
    }

    /**
     * @param key The key of the message
     * @return The message
     */
    @Nullable
    public ReferenceBlobMessage getMessage(String key) {
        return getMessageManager().getMessage(key);
    }

    /**
     * @param key    The key of the message
     * @param locale The locale of the message
     * @return The message
     */
    @Nullable
    public ReferenceBlobMessage getMessage(String key, String locale) {
        return getMessageManager().getMessage(key, locale);
    }

    /**
     * @param key    The key of the message
     * @param locale The locale of the message
     * @return The message, or the default message if not found
     */
    @Nullable
    public ReferenceBlobMessage getLocaleMessageOrDefault(String key, String locale) {
        ReferenceBlobMessage localeMessage = getMessageManager().getMessage(key, locale);
        if (localeMessage != null)
            return localeMessage;
        return getMessageManager().getMessage(key);
    }

    /**
     * Gets the locale of the player and returns the message.
     *
     * @param key    The key of the message
     * @param player The player to get the locale from
     * @return The message, or the default message if not found
     */
    @Nullable
    public ReferenceBlobMessage getLocaleMessageOrDefault(String key, Player player) {
        String locale = player.getLocale();
        return getLocaleMessageOrDefault(key, locale);
    }

    /**
     * @param key    The key of the message
     * @param player The player to send the message to
     */
    public void sendMessage(String key, Player player) {
        getMessageManager().send(player, key);
    }

}
