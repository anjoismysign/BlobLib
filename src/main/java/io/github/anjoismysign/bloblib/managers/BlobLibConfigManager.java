package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.entities.ConfigDecorator;
import io.github.anjoismysign.bloblib.entities.ListenersSection;
import io.github.anjoismysign.bloblib.entities.LocaleDefault;
import io.github.anjoismysign.bloblib.entities.TinyEventListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BlobLibConfigManager {
    private static BlobLibConfigManager instance;

    public static BlobLibConfigManager getInstance(BlobLib director) {
        if (instance == null) {
            if (director == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibConfigManager.instance = new BlobLibConfigManager(director);
        }
        return instance;
    }

    public static BlobLibConfigManager getInstance() {
        return getInstance(null);
    }

    private final BlobLib plugin;
    private TinyEventListener displayRiding;
    private String consoleLocale;
    private Map<String, String> defaultLocale;
    private boolean verbose;
    private TranslatableRarities rarities;

    private BlobLibConfigManager(BlobLib plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        defaultLocale = new HashMap<>();
        plugin.reloadConfig();
        plugin.saveDefaultConfig();
        ConfigDecorator configDecorator = new ConfigDecorator(plugin);
        ConfigurationSection section = configDecorator.reloadAndGetSection("Settings");
        verbose = section.getBoolean("Verbose", false);
        ConfigurationSection locale = configDecorator.reloadAndGetSection("Locale");
        consoleLocale = locale.getString("Console");
        Set<LocaleDefault> set = LocaleDefault.READ(
                locale.getConfigurationSection("Default-To"));
        set.forEach(localeDefault -> {
            String to = localeDefault.to();
            List<String> from = localeDefault.from();
            from.forEach(fromLocale -> {
                if (defaultLocale.containsKey(fromLocale)) {
                    BlobLib.getAnjoLogger().singleError("Duplicate default description for " + fromLocale);
                    return;
                }
                defaultLocale.put(fromLocale, to);
            });
        });
        ListenersSection listenersSection = configDecorator.reloadAndGetListeners();
        displayRiding = listenersSection.tinyEventListener("Display-Unriding");

        plugin.saveResource("rarity.yml", false);
        File rarity = new File(plugin.getDataFolder(), "rarity.yml");
        rarities = TranslatableRarities.of(YamlConfiguration.loadConfiguration(rarity));
    }

    public TinyEventListener getDisplayRiding() {
        return displayRiding;
    }

    /**
     * Will return the locale to use for console messages.
     *
     * @return the locale to use for console messages
     */
    public String getConsoleLocale() {
        return consoleLocale;
    }

    /**
     * Returns the locale to use for console messages.
     *
     * @param locale the locale to use
     * @return the locale to use for console messages
     */
    public String getRealLocale(String locale) {
        return defaultLocale.getOrDefault(locale, locale);
    }

    public boolean isVerbose() {
        return verbose;
    }

    public TranslatableRarities getRarities() {
        return rarities;
    }
}