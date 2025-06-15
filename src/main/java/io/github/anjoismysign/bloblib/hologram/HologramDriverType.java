package io.github.anjoismysign.bloblib.hologram;

public enum HologramDriverType {
    DECENT_HOLOGRAMS,
    FANCY_HOLOGRAMS,
    ABSENT;

    public boolean isAbsent() {
        return this == ABSENT;
    }
}
