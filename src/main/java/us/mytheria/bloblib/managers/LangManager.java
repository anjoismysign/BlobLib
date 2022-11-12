package us.mytheria.bloblib.managers;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import us.mytheria.bloblib.BlobLib;

import java.util.HashMap;

public class LangManager {
    private final String msg = "Message.";
    private final String actionbar = "Action-Bar.";
    private final String title = "Title.";
    private final String menu = "Menu.";
    private BlobLib main;

    private HashMap<String, String> lang;

    public LangManager() {
        this.main = BlobLib.getInstance();
        load();
    }

    public static void sub(Player player, String subtitle) {
        player.sendTitle(" ", ChatColor.translateAlternateColorCodes('&', subtitle), 10, 60, 10);
    }

    public void actionBar(Player player, String key, Sound sound, float volume, float pitch) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(getLang(key)));
        player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
    }

    public void actionBar(Player player, String key) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(getLang(key)));
    }

    public String[] getTitle(String key) {
        return getLang(key).split("%split%");
    }

    public void sendActionBar(Player player, String key) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(getLang(key)));
    }

    /**
     * Will send a title to player with a default of
     * 10 ticks fade in, 40 ticks stay, 10 ticks fade out
     *
     * @param player player to send title
     * @param key    key to get title from
     */
    public void title(Player player, String key) {
        title(player, key, 10, 40, 10);
    }

    public void replaceSubtitle(Player player, String key, String regex, String replacement) {
        String[] title = getTitle(key);
        player.sendTitle(title[0], title[1].replace(regex, replacement), 10, 40, 10);
    }

    public void replaceSubtitle(Player player, String key, String regex, String replacement, int fadeIn, int stay, int fadeOut) {
        String[] title = getTitle(key);
        player.sendTitle(title[0], title[1].replace(regex, replacement), fadeIn, stay, fadeOut);
    }

    public void title(Player player, String key, int fadeIn, int stay, int fadeOut) {
        String[] title = getTitle(key);
        player.sendTitle(title[0], title[1], fadeIn, stay, fadeOut);
    }

    public void load() {
        lang = new HashMap<>();
        YamlConfiguration langYml = main.getFileManager().getYml(main.getFileManager().getLang());
        ConfigurationSection msgSection = langYml.getConfigurationSection(msg);
        msgSection.getKeys(true).forEach(key -> {
            if (msgSection.isConfigurationSection(key))
                return;
            lang.put("msg." + key, ChatColor.translateAlternateColorCodes('&', langYml.getString(msg + key)));
        });
        ConfigurationSection actionbarSection = langYml.getConfigurationSection(actionbar);
        if (actionbarSection != null)
            actionbarSection.getKeys(true).forEach(key -> {
                if (actionbarSection.isConfigurationSection(key))
                    return;
                lang.put("actionbar." + key, ChatColor.translateAlternateColorCodes('&', langYml.getString(actionbar + key)));
            });
        ConfigurationSection titleSection = langYml.getConfigurationSection(title);
        titleSection.getKeys(true).forEach(key -> {
            String value = langYml.getString(title + key);
            if (titleSection.isConfigurationSection(value))
                return;
            lang.put("title." + key, ChatColor.translateAlternateColorCodes('&', langYml.getString(title + key)));
        });
        ConfigurationSection menuSection = langYml.getConfigurationSection(menu);
        menuSection.getKeys(true).forEach(key -> {
            if (menuSection.isConfigurationSection(key))
                return;
            lang.put("menu." + key, ChatColor.translateAlternateColorCodes('&', langYml.getString(menu + key)));
        });
    }

    public String noPermission() {
        return lang.get("msg.No-Permission");
    }

    public String getLang(String key) {
        return lang.get(key);
    }
}
