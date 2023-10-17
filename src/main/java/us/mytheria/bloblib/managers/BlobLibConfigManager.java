package us.mytheria.bloblib.managers;

import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.ConfigDecorator;
import us.mytheria.bloblib.entities.ListenersSection;
import us.mytheria.bloblib.entities.LocaleDefault;
import us.mytheria.bloblib.entities.TinyEventListener;

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

    private BlobLibConfigManager(BlobLib plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        defaultLocale = new HashMap<>();
        plugin.reloadConfig();
        plugin.saveDefaultConfig();
        ConfigDecorator configDecorator = new ConfigDecorator(plugin);
        ConfigurationSection locale = configDecorator.reloadAndGetSection("Locale");
        consoleLocale = locale.getString("Console");
        Set<LocaleDefault> set = LocaleDefault.READ(
                locale.getConfigurationSection("Default-To"));
        set.forEach(localeDefault -> {
            String to = localeDefault.to();
            List<String> from = localeDefault.from();
            from.forEach(fromLocale -> {
                if (defaultLocale.containsKey(fromLocale)) {
                    BlobLib.getAnjoLogger().singleError("Duplicate default locale for " + fromLocale);
                    return;
                }
                defaultLocale.put(fromLocale, to);
            });
        });
        ListenersSection listenersSection = configDecorator.reloadAndGetListeners();
        displayRiding = listenersSection.tinyEventListener("Display-Unriding");
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
}