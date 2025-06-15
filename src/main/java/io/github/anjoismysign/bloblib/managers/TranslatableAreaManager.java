package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.entities.DataAssetType;
import io.github.anjoismysign.bloblib.entities.area.Area;
import io.github.anjoismysign.bloblib.entities.area.AreaIO;
import io.github.anjoismysign.bloblib.entities.area.AreaType;
import io.github.anjoismysign.bloblib.entities.area.BoxArea;
import io.github.anjoismysign.bloblib.entities.translatable.BlobTranslatableArea;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableArea;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TranslatableAreaManager extends LocalizableDataAssetManager<TranslatableArea> {

    /**
     * Creates a new instance of the LocalizableDataAssetManager
     *
     * @return The new instance of the LocalizableDataAssetManager
     */
    public static TranslatableAreaManager of() {
        return new TranslatableAreaManager(BlobLib.getInstance().getFileManager().getDirectory(DataAssetType.TRANSLATABLE_AREA),
                (section, locale, key) -> {
                    if (locale.equalsIgnoreCase("en_us")) {
                        Area area = AreaIO.INSTANCE.read(section);
                        if (!section.isString("Display"))
                            throw new ConfigurationFieldException("'Display' is missing or not set");
                        String display = section.getString("Display");
                        return BlobTranslatableArea.of(key, locale, display, area);
                    } else {
                        if (!section.isString("Display"))
                            throw new ConfigurationFieldException("'Display' is missing or not set");
                        String display = section.getString("Display");
                        return TranslatableArea.forLocale(key, locale, display);
                    }
                },
                DataAssetType.TRANSLATABLE_AREA,
                section -> section.isString("World"));
    }

    TranslatableAreaManager(@NotNull File assetDirectory, @NotNull TriFunction<ConfigurationSection, String, String, TranslatableArea> readFunction, @NotNull DataAssetType type, Predicate<ConfigurationSection> filter) {
        super(assetDirectory, readFunction, type, filter);
    }

    public List<TranslatableArea> unorderedContains(@NotNull Location location) {
        return getDefault().values()
                .stream()
                .filter(translatableArea -> {
                    Area area = translatableArea.get();
                    return area.isInside(location);
                })
                .toList();
    }

    public List<TranslatableArea> orderedContains(@NotNull Location location) {
        Map<String, TranslatableArea> defaultAreas = getDefault();
        return defaultAreas.values()
                .stream()
                .filter(translatableArea -> {
                    Area area = translatableArea.get();
                    return area.getType() == AreaType.BOX_AREA && area.isInside(location);
                })
                .map(translatableArea -> Map.entry(translatableArea, (BoxArea) translatableArea.get()))
                .sorted((entry1, entry2) -> {
                    BoxArea firstArea = entry1.getValue();
                    BoxArea secondArea = entry2.getValue();
                    if (firstArea.contains(secondArea)) {
                        return -1;
                    }
                    if (secondArea.contains(firstArea)) {
                        return 1;
                    }
                    return 0;
                })
                .map(Map.Entry::getKey)
                .toList();
    }
}
