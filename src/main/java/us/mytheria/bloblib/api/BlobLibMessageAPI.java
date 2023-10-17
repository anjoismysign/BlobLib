package us.mytheria.bloblib.api;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.message.ReferenceBlobMessage;
import us.mytheria.bloblib.managers.BlobLibConfigManager;
import us.mytheria.bloblib.managers.MessageManager;

import java.io.File;
import java.util.Objects;

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
     * Gets the message from the default locale.
     *
     * @param key The key of the message
     * @return The message
     * @deprecated Use {@link #getMessage(String, String)} instead
     */
    @Deprecated
    @Nullable
    public ReferenceBlobMessage getMessage(@NotNull String key) {
        Objects.requireNonNull(key);
        return getMessageManager().getMessage(key);
    }

    /**
     * Gets the message from the locale.
     * If message is not available in the locale, returns the default message.
     *
     * @param key    The key of the message
     * @param locale The locale of the message
     * @return The message
     */
    @Nullable
    public ReferenceBlobMessage getMessage(@NotNull String key, @NotNull String locale) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(locale);
        return getMessageManager().getMessage(key, locale);
    }

    /**
     * Gets the locale of the player and returns the message.
     * If message is not available in the locale, returns the default message.
     *
     * @param key    The key of the message
     * @param player The player to get the locale from
     * @return The message
     * @deprecated Use {@link #getMessage(String, CommandSender)} instead
     */
    @Deprecated
    @Nullable
    public ReferenceBlobMessage getMessage(@NotNull String key, @NotNull Player player) {
        Objects.requireNonNull(player);
        return getMessageManager().getMessage(key, player.getLocale());
    }

    /**
     * Gets the locale of the command sender and returns the message.
     *
     * @param key    The key of the message
     * @param sender The command sender to get the locale from
     * @return The message
     */
    @Nullable
    public ReferenceBlobMessage getMessage(@NotNull String key, @NotNull CommandSender sender) {
        Objects.requireNonNull(sender);
        return getMessageManager().getMessage(key, sender instanceof Player ? ((Player) sender).getLocale() :
                BlobLibConfigManager.getInstance().getConsoleLocale());
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
