package io.github.anjoismysign.bloblib.storage;

import java.io.File;

public record FileDetachment(File file, boolean isFresh) {
}
