package io.github.anjoismysign.bloblib.entities;

public class ArrayNavigator<T> {
    private final T[] array;
    private int index;

    public static <T> ArrayNavigator<Boolean> ofBoolean() {
        return new ArrayNavigator<>(new Boolean[]{false, true});
    }

    public static <T> ArrayNavigator<Boolean> defaultTrueBoolean() {
        return new ArrayNavigator<>(new Boolean[]{true, false});
    }

    public ArrayNavigator(T[] array) {
        this.array = array;
        this.index = 0;
    }

    public T next() {
        index = (index + 1) % array.length;
        return array[index];
    }

    public T previous() {
        index = (index - 1 + array.length) % array.length;
        return array[index];
    }

    public T current() {
        return array[index];
    }
}
