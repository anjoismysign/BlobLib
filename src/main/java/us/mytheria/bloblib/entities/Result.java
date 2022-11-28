package us.mytheria.bloblib.entities;

public record Result<T>(T value, boolean isValid) {
    public static <T> Result<T> valid(T value) {
        return new Result<>(value, true);
    }

    public static <T> Result<T> invalid(T value) {
        return new Result<>(value, false);
    }
}
