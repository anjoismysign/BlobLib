package us.mytheria.bloblib.entities;

import org.jetbrains.annotations.NotNull;

public interface BlobCrudableSerializer {
    @NotNull
    BlobCrudable serialize();
}
