package io.github.anjoismysign.bloblib.entities;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.managers.BlobPlugin;
import io.github.anjoismysign.bloblib.managers.InventoryManager;
import io.github.anjoismysign.bloblib.managers.MessageManager;
import io.github.anjoismysign.bloblib.managers.SoundManager;
import io.github.anjoismysign.bloblib.managers.TranslatableManager;
import io.github.anjoismysign.anjo.entities.NamingConventions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public enum DataAssetType {
    BLOB_MESSAGE("messages", File.separator + "BlobMessage", "_lang.yml",
            (plugin, files) -> MessageManager.continueLoadingMessages(plugin, true, files.toArray(new File[0]))),
    BLOB_SOUND("sounds", File.separator + "BlobSound", "_sounds.yml",
            (plugin, files) -> SoundManager.continueLoadingSounds(plugin, true, files.toArray(new File[0]))),
    BLOB_INVENTORY("blobInventories", File.separator + "BlobInventory", "_inventories.yml",
            (plugin, files) -> InventoryManager.continueLoadingBlobInventories(plugin, files.toArray(new File[0]))),
    META_BLOB_INVENTORY("metaBlobInventories", File.separator + "MetaBlobInventory", "_meta_inventories.yml",
            (plugin, files) -> InventoryManager.continueLoadingMetaInventories(plugin, files.toArray(new File[0]))),
    ACTION("actions", File.separator + "Action", "_actions.yml",
            (plugin, files) -> {
            }),
    TRANSLATABLE_BLOCK("translatableBlocks", File.separator + "TranslatableBlock", "_translatable_blocks.yml",
            (plugin, files) -> TranslatableManager.continueLoadingBlocks(plugin, true, files.toArray(new File[0]))),
    TRANSLATABLE_SNIPPET("translatableSnippets", File.separator + "TranslatableSnippet", "_translatable_snippets.yml",
            (plugin, files) -> TranslatableManager.continueLoadingSnippets(plugin, true, files.toArray(new File[0]))),
    TRANSLATABLE_ITEM("translatableItems", File.separator + "TranslatableItem", "_translatable_items.yml",
            (plugin, files) -> BlobLib.getInstance().getTranslatableItemManager().continueLoadingAssets(plugin, true, files.toArray(new File[0]))),
    TAG_SET("tagSets", File.separator + "TagSet", "_tag_sets.yml",
            (plugin, files) -> BlobLib.getInstance().getTagSetManager().continueLoadingAssets(plugin, true, files.toArray(new File[0]))),
    TRANSLATABLE_POSITIONABLE("translatablePositionables", File.separator + "TranslatablePositionable", "_translatable_positionables.yml",
            (plugin, files) -> BlobLib.getInstance().getTranslatablePositionableManager().continueLoadingAssets(plugin, true, files.toArray(new File[0]))),
    TRANSLATABLE_AREA("translatableAreas", File.separator + "TranslatableArea", "_translatable_areas.yml",
            (plugin, files) -> BlobLib.getInstance().getTranslatableAreaManager().continueLoadingAssets(plugin, true, files.toArray(new File[0])));

    private static final Map<String, DataAssetType> byEqualsIgnoreObjectName = new HashMap<>();

    static {
        for (DataAssetType assetType : values()) {
            byEqualsIgnoreObjectName.put(assetType.getObjectName().toLowerCase(Locale.ROOT), assetType);
        }
    }

    @NotNull
    private final String key, directoryPath, defaultFilePath;
    @NotNull
    private final BiConsumer<BlobPlugin, List<File>> continueLoading;

    @Nullable
    public static DataAssetType byEqualsIgnoreObjectName(@NotNull String objectName) {
        Objects.requireNonNull(objectName, "'objectName' cannot be null");
        return byEqualsIgnoreObjectName.get(objectName.toLowerCase(Locale.ROOT));
    }

    DataAssetType(
            @NotNull String key,
            @NotNull String directoryPath,
            @NotNull String defaultFilePath,
            @NotNull BiConsumer<BlobPlugin, List<File>> continueLoading) {
        this.key = key;
        this.directoryPath = directoryPath;
        this.defaultFilePath = defaultFilePath;
        this.continueLoading = continueLoading;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @NotNull
    public String getDefaultFileKey() {
        String pascalKey = NamingConventions.toPascalCase(key);
        return "default" + pascalKey;
    }

    @NotNull
    public String getDefaultFilePath() {
        return defaultFilePath;
    }

    @NotNull
    public String getDirectoryPath() {
        return directoryPath;
    }

    public @NotNull BiConsumer<BlobPlugin, List<File>> getContinueLoading() {
        return continueLoading;
    }

    public String getObjectName() {
        return name().replace("_", "");
    }
}
