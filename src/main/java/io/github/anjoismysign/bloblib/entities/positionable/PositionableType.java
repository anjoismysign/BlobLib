package io.github.anjoismysign.bloblib.entities.positionable;

public enum PositionableType {
    POSITIONABLE,
    SPATIAL,
    LOCATABLE;

    /**
     * Whether this PositionableType is instance of Locatable
     *
     * @return true if so, false otherwise
     */
    public boolean isLocatable() {
        return this == LOCATABLE;
    }

    /**
     * Whether this PositionableType is instance of Spatial
     *
     * @return true if so, false otherwise
     */
    public boolean isSpatial() {
        return this != POSITIONABLE;
    }
}
