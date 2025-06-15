package io.github.anjoismysign.bloblib.entities;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface MutableAddress<T> extends Address<T> {

    static <T> MutableAddress<T> of(@Nullable T value) {
        List<T> list = new ArrayList<>(1);
        list.add(value);
        return new MutableAddress<T>() {
            @Override
            public void set(T object) {
                list.set(0, object);
            }

            @Override
            public @Nullable T look() {
                return list.get(0);
            }
        };
    }

    static <T> MutableAddress<T> nullable() {
        return of(null);
    }

    void set(T object);
}
