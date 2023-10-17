package us.mytheria.bloblib.entities;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record LocaleDefault(String to, List<String> from) {

    /**
     * Creates a new LocaleDefault object.
     *
     * @param to   The locale to translate to.
     * @param from The locales to translate from.
     * @return A new LocaleDefault object.
     */
    public static LocaleDefault of(String to, String... from) {
        return new LocaleDefault(to, List.of(from));
    }

    public static Set<LocaleDefault> READ(ConfigurationSection section) {
        Set<LocaleDefault> set = new HashSet<>();
        section.getKeys(false).forEach(to -> {
            List<String> from = section.getStringList(to);
            set.add(new LocaleDefault(to, from));
        });
        return set;
    }
}
