package io.github.anjoismysign.bloblib.entities;

import org.jetbrains.annotations.Nullable;

public interface Address<T> {

    @Nullable
    T look();

}
