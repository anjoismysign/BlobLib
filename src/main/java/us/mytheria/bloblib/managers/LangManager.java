package us.mytheria.bloblib.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.BlobMessageReader;
import us.mytheria.bloblib.entities.message.BlobMessage;

import java.util.HashMap;

public class LangManager {
    private final BlobLib main;
    private HashMap<String, BlobMessage> lang;

    public LangManager() {
        this.main = BlobLib.getInstance();
        load();
    }

    public void load() {
        lang = new HashMap<>();
        YamlConfiguration langYml = main.getFileManager().getYml(main.getFileManager().langFile());
        langYml.getKeys(false).forEach(key -> {
            ConfigurationSection section = langYml.getConfigurationSection(key);
            section.getKeys(true).forEach(subKey -> {
                if (section.isConfigurationSection(subKey))
                    return;
                ConfigurationSection subSection = section.getConfigurationSection(subKey);
                lang.put(key + "." + subKey, BlobMessageReader.read(subSection));
            });
        });
    }

    public void noPermission(Player player) {
        lang.get("Message.No-Permission").sendAndPlay(player);
    }

    public BlobMessage getLang(String key) {
        return lang.get(key);
    }

    public void playAndSend(Player player, String key) {
        BlobMessage message = lang.get(key);
        if (message == null)
            throw new NullPointerException("Message '" + key + "' does not exist!");
        message.sendAndPlay(player);
    }

    public void send(Player player, String key) {
        BlobMessage message = lang.get(key);
        if (message == null)
            throw new NullPointerException("Message '" + key + "' does not exist!");
        message.send(player);
    }
}
