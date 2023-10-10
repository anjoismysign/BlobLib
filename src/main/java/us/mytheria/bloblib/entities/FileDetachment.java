package us.mytheria.bloblib.entities;

import java.io.File;

public record FileDetachment(File file, boolean isFresh) {
}
