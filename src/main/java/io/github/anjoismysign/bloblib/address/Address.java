package io.github.anjoismysign.bloblib.address;

import org.jetbrains.annotations.Nullable;

public interface Address<T> {

    @Nullable
    T look();

}
