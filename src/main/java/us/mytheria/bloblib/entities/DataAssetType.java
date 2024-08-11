package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.NamingConventions;
import org.jetbrains.annotations.NotNull;

public enum DataAssetType {
    BLOB_MESSAGE("messages", "/BlobMessage", "_lang.yml"),
    BLOB_SOUND("sounds", "/BlobSound", "_sounds.yml"),
    BLOB_INVENTORY("blobInventories", "/BlobInventory", "_inventories.yml"),
    META_BLOB_INVENTORY("metaBlobInventories", "/MetaBlobInventory", "_meta_inventories.yml"),
    ACTION("actions", "/Action", "_actions.yml"),
    TRANSLATABLE_BLOCK("translatableBlocks", "/TranslatableBlock", "_translatable_blocks.yml"),
    TRANSLATABLE_SNIPPET("translatableSnippets", "/TranslatableSnippet", "_translatable_snippets.yml"),
    TRANSLATABLE_ITEM("translatableItems", "/TranslatableItem", "_translatable_items.yml"),
    TAG_SET("tagSets", "/TagSet", "_tag_sets.yml"),
    TRANSLATABLE_POSITIONABLE("translatablePositionables", "/TranslatablePositionable", "_translatable_positionables.yml");

    @NotNull
    private final String key, directoryPath, defaultFilePath;

    private DataAssetType(
            @NotNull String key,
            @NotNull String directoryPath,
            @NotNull String defaultFilePath) {
        this.key = key;
        this.directoryPath = directoryPath;
        this.defaultFilePath = defaultFilePath;
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

    public String getObjectName() {
        return name().replace("_", "");
    }
}
