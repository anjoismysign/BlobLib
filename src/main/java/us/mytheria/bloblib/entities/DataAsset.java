package us.mytheria.bloblib.entities;

public interface DataAsset {
    /**
     * Returns the key of the data asset.
     *
     * @return The key of the data asset.
     */
    String getReference();

    /**
     * Returns the type of the data asset.
     *
     * @return The type of the data asset.
     */
    DataAssetType getType();
}
