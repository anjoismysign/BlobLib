package io.github.anjoismysign.bloblib.manager;

import io.github.anjoismysign.holoworld.asset.DataAsset;

import java.io.File;

public interface BlobLibDataAssetManager<T extends DataAsset> {

    void reload();

    void reload(BlobPlugin plugin, IManagerDirector director);

    void unload(BlobPlugin plugin);

    void continueLoadingAssets(BlobPlugin plugin, boolean warnDuplicates, File... files);

}
