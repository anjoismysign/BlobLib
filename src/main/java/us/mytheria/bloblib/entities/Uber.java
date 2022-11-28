package us.mytheria.bloblib.entities;

public class Uber<T> {
    private T value;

    public static <T> Uber<T> drive(T value) {
        return new Uber<>(value);
    }

    public Uber(T value) {
        this.value = value;
    }

    public T stop() {
        return value;
    }

    public void talk(T value) {
        this.value = value;
    }
}
