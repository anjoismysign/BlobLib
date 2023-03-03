package us.mytheria.bloblib.entities;

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
        if (index >= array.length) {
            index = 0;
        }
        return array[index++];
    }

    public T previous() {
        if (index <= 0) {
            index = array.length - 1;
        }
        return array[index--];
    }

    public T current() {
        return array[index];
    }
}
