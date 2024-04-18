package us.mytheria.bloblib.entities;

public enum DataAssetType {
    BLOB_MESSAGE,
    BLOB_SOUND,
    BLOB_INVENTORY,
    META_BLOB_INVENTORY,
    ACTION,
    TRANSLATABLE_BLOCK,
    TRANSLATABLE_SNIPPET,
    TRANSLATABLE_ITEM,
    TAG_SET;

    public String getObjectName() {
        return name().replace("_", "");
    }
}
