package us.mytheria.bloblib.entities;

import java.io.File;

public interface BlobObject {
    String getKey();

    File saveToFile();
}
