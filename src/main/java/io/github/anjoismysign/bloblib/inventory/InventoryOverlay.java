package io.github.anjoismysign.bloblib.inventory;

import io.github.anjoismysign.bloblib.content.ContentWarning;
import io.github.anjoismysign.bloblib.content.ContentWarningRegistry;
import io.github.anjoismysign.bloblib.content.LocaleOverlay;
import io.github.anjoismysign.bloblib.domain.DataAssetType;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * The locale overlay of an inventory: a non default locale file that carries the
 * inventory's translatable text alone, inheriting size, buttons, slots, permissions,
 * prices and actions from the en_us file of the same reference.
 * <p>
 * An overlay is not built while its file is read, since the en_us file it inherits
 * from is not guaranteed to have loaded yet. It is kept as a {@link Pending} until
 * every plugin has loaded its content, and then merged.
 * <p>
 * Naming a button in an overlay replaces that button's whole text triple: the base's
 * 'DisplayName', 'ItemName' and 'Lore' are dropped, and only what the overlay declares
 * comes back. This is the same rule a TranslatableItem overlay follows.
 */
public final class InventoryOverlay {

    public static final String TITLE = "Title";
    public static final String BUTTONS = "Buttons";
    public static final String ITEM_STACK = "ItemStack";

    /**
     * The fields an overlay is read for at the root of an inventory.
     */
    private static final Set<String> ROOT_FIELDS = Set.of(TITLE, BUTTONS);

    /**
     * The fields an overlay is read for inside a button.
     */
    private static final Set<String> BUTTON_FIELDS = Set.of(ITEM_STACK);

    /**
     * The fields an overlay is read for inside a button's ItemStack.
     */
    private static final Set<String> TEXT_FIELDS = Set.of("DisplayName", "ItemName", "Lore", "minimessage");

    /**
     * The fields a named button loses before the overlay's own text is applied.
     */
    private static final Set<String> WIPED_FIELDS = Set.of("DisplayName", "ItemName", "Lore");

    private InventoryOverlay() {
    }

    /**
     * An overlay that has been read and accepted, but not merged yet.
     *
     * @param type      The type of inventory the overlay belongs to
     * @param reference The identifier of the inventory
     * @param locale    The locale of the overlay file
     * @param filePath  The path of the overlay file
     * @param section   The section the overlay was read from
     */
    public record Pending(@NotNull DataAssetType type,
                          @NotNull String reference,
                          @NotNull String locale,
                          @NotNull String filePath,
                          @NotNull ConfigurationSection section) {
    }

    /**
     * Reads an overlay, rejecting it if it would translate nothing and registering a
     * {@link ContentWarning} for every field it declares that only the en_us file is read for.
     *
     * @param type      The type of inventory the overlay belongs to
     * @param section   The section the overlay is read from
     * @param locale    The locale of the overlay file
     * @param reference The identifier of the inventory
     * @param filePath  The path of the overlay file
     * @return The overlay, waiting to be merged
     * @throws ConfigurationFieldException If the overlay declares no translatable text at all
     */
    @NotNull
    public static Pending read(@NotNull DataAssetType type,
                               @NotNull ConfigurationSection section,
                               @NotNull String locale,
                               @NotNull String reference,
                               @NotNull String filePath) {
        Objects.requireNonNull(type, "'type' cannot be null");
        Objects.requireNonNull(section, "'section' cannot be null");
        Objects.requireNonNull(locale, "'locale' cannot be null");
        Objects.requireNonNull(reference, "'reference' cannot be null");
        Objects.requireNonNull(filePath, "'filePath' cannot be null");
        boolean hasTitle = section.isString(TITLE);
        @Nullable ConfigurationSection buttons = section.getConfigurationSection(BUTTONS);
        boolean hasText = false;
        if (buttons != null) {
            for (String key : buttons.getKeys(false)) {
                @Nullable ConfigurationSection button = buttons.getConfigurationSection(key);
                if (button == null)
                    continue;
                @Nullable ConfigurationSection itemStack = button.getConfigurationSection(ITEM_STACK);
                if (itemStack == null)
                    continue;
                for (String field : WIPED_FIELDS) {
                    if (itemStack.contains(field)) {
                        hasText = true;
                        break;
                    }
                }
            }
        }
        if (!hasTitle && !hasText)
            throw new ConfigurationFieldException("'" + reference + "' declares neither a 'Title' nor any button " +
                    "'DisplayName', 'ItemName' or 'Lore', so it would translate nothing. A '" + locale + "' file " +
                    "carries translatable text alone: every other field is inherited from the en_us file and is " +
                    "ignored here. This file is rejected on purpose, because a translation that translates nothing " +
                    "is almost always a mistake. Either add text to translate, or delete the file.");
        LocaleOverlay.warnStrayFields(type, reference, locale, filePath, section, ROOT_FIELDS);
        if (buttons != null) {
            for (String key : buttons.getKeys(false)) {
                @Nullable ConfigurationSection button = buttons.getConfigurationSection(key);
                if (button == null)
                    continue;
                String buttonPath = BUTTONS + "." + key;
                LocaleOverlay.warnStrayFields(type, reference, locale, filePath, button, BUTTON_FIELDS, buttonPath);
                @Nullable ConfigurationSection itemStack = button.getConfigurationSection(ITEM_STACK);
                if (itemStack == null)
                    continue;
                LocaleOverlay.warnStrayFields(type, reference, locale, filePath, itemStack, TEXT_FIELDS,
                        buttonPath + "." + ITEM_STACK);
            }
        }
        return new Pending(type, reference, locale, filePath, section);
    }

    /**
     * Merges an overlay onto the section its en_us counterpart was read from, so that the
     * result can be read by the very same factory the en_us file is read by.
     *
     * @param base    The section of the en_us file
     * @param overlay The overlay to apply
     * @return A new section, leaving both arguments untouched
     */
    @NotNull
    public static ConfigurationSection merge(@NotNull ConfigurationSection base,
                                             @NotNull Pending overlay) {
        Objects.requireNonNull(base, "'base' cannot be null");
        Objects.requireNonNull(overlay, "'overlay' cannot be null");
        YamlConfiguration merged = new YamlConfiguration();
        copyInto(base, merged);
        ConfigurationSection section = overlay.section();
        if (section.isString(TITLE))
            merged.set(TITLE, section.getString(TITLE));
        merged.set(LocaleOverlay.LOCALE_FIELD, overlay.locale());
        @Nullable ConfigurationSection buttons = section.getConfigurationSection(BUTTONS);
        if (buttons == null)
            return merged;
        @Nullable ConfigurationSection mergedButtons = merged.getConfigurationSection(BUTTONS);
        for (String key : buttons.getKeys(false)) {
            @Nullable ConfigurationSection button = buttons.getConfigurationSection(key);
            if (button == null)
                continue;
            @Nullable ConfigurationSection itemStack = button.getConfigurationSection(ITEM_STACK);
            if (itemStack == null)
                continue;
            String buttonPath = BUTTONS + "." + key;
            @Nullable ConfigurationSection target = mergedButtons == null
                    ? null : mergedButtons.getConfigurationSection(key);
            if (target == null) {
                warn(overlay, buttonPath, "'" + key + "' is not a button of the en_us '" + overlay.reference() +
                        "', so it was skipped. An overlay translates the buttons of its en_us file, it cannot add new ones.");
                continue;
            }
            if (target.isString(ITEM_STACK)) {
                warn(overlay, buttonPath + "." + ITEM_STACK, "'" + key + "' points at the TranslatableItem '" +
                        target.getString(ITEM_STACK) + "' in the en_us file, which translates itself. The text " +
                        "written here was ignored, it belongs in that TranslatableItem's own '" + overlay.locale() + "' file.");
                continue;
            }
            @Nullable ConfigurationSection targetStack = target.getConfigurationSection(ITEM_STACK);
            if (targetStack == null) {
                warn(overlay, buttonPath + "." + ITEM_STACK, "'" + key + "' has no 'ItemStack' in the en_us file, " +
                        "so there was nothing to translate and the text written here was ignored.");
                continue;
            }
            for (String field : WIPED_FIELDS)
                targetStack.set(field, null);
            for (String field : TEXT_FIELDS) {
                if (itemStack.contains(field))
                    targetStack.set(field, itemStack.get(field));
            }
        }
        return merged;
    }

    /**
     * Deep copies a section, which Bukkit offers no way of doing on its own.
     *
     * @param from The section to copy
     * @param to   The section to copy into
     */
    public static void copyInto(@NotNull ConfigurationSection from,
                                @NotNull ConfigurationSection to) {
        Objects.requireNonNull(from, "'from' cannot be null");
        Objects.requireNonNull(to, "'to' cannot be null");
        for (String key : from.getKeys(false)) {
            Object value = from.get(key);
            if (value instanceof ConfigurationSection child)
                copyInto(child, to.createSection(key));
            else
                to.set(key, value);
        }
    }

    private static void warn(@NotNull Pending overlay,
                             @NotNull String field,
                             @NotNull String reason) {
        ContentWarningRegistry.INSTANCE.register(new ContentWarning(overlay.type(), overlay.reference(),
                overlay.locale(), overlay.filePath(), field, reason));
    }
}
