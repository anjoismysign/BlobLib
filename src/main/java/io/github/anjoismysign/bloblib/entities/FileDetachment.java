package io.github.anjoismysign.bloblib.entities;

import java.io.File;

public record FileDetachment(File file, boolean isFresh) {
}
