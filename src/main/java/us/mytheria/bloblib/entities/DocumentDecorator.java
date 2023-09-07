package us.mytheria.bloblib.entities;

import org.bson.Document;
import org.bson.types.Binary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record DocumentDecorator(Document document) {

    @NotNull
    public static DocumentDecorator deserialize(byte[] bytes) {
        DocumentDecorator decorator = new DocumentDecorator(Document.parse(new String(bytes)));
        return Objects.requireNonNull(decorator, "Failed to deserialize document");
    }

    /**
     * Will get an object from the document.
     *
     * @param key the key
     * @return the object
     */
    public Optional<Object> has(String key) {
        return Optional.ofNullable(document.get(key));
    }

    /**
     * Will get a long from the document.
     *
     * @param key the key
     * @return the long
     */
    public Optional<Long> hasLong(String key) {
        return Optional.ofNullable(document.getLong(key));
    }

    /**
     * Will get a float from the document.
     *
     * @param key the key
     * @return the float
     */
    public Optional<Float> hasFloat(String key) {
        Float value = document.get(key, Float.class);
        if (value == null)
            return Optional.empty();
        return Optional.ofNullable(value);
    }

    /**
     * Will get a double from the document.
     *
     * @param key the key
     * @return the double
     */
    public Optional<Double> hasDouble(String key) {
        return Optional.ofNullable(document.getDouble(key));
    }

    /**
     * Will get an integer from the document.
     *
     * @param key the key
     * @return the integer
     */
    public Optional<Integer> hasInteger(String key) {
        return Optional.ofNullable(document.getInteger(key));
    }

    /**
     * Will get a short from the document.
     *
     * @param key the key
     * @return the short
     */
    public Optional<Short> hasShort(String key) {
        Short value = document.get(key, Short.class);
        if (value == null)
            return Optional.empty();
        return Optional.ofNullable(value);
    }

    /**
     * Will get a byte from the document.
     *
     * @param key the key
     * @return the byte
     */
    public Optional<Byte> hasByte(String key) {
        Byte value = document.get(key, Byte.class);
        if (value == null)
            return Optional.empty();
        return Optional.ofNullable(value);
    }

    /**
     * Will get a string from the document.
     *
     * @param key the key
     * @return the string
     */
    public Optional<String> hasString(String key) {
        return Optional.ofNullable(document.getString(key));
    }

    /**
     * Will get a character from the document.
     *
     * @param key the key
     * @return the character
     */
    public Optional<Character> hasCharacter(String key) {
        Character value = document.get(key, Character.class);
        if (value == null)
            return Optional.empty();
        return Optional.ofNullable(value);
    }

    /**
     * Will get a boolean from the document.
     *
     * @param key the key
     * @return the boolean
     */
    public Optional<Boolean> hasBoolean(String key) {
        return Optional.ofNullable(document.getBoolean(key));
    }

    /**
     * Will get a long list from the document.
     *
     * @param key the key
     * @return the long list
     */
    public Optional<List<Long>> hasLongList(String key) {
        return Optional.ofNullable(document.getList(key, Long.class));
    }

    /**
     * Will get a float list from the document.
     *
     * @param key the key
     * @return the float list
     */
    public Optional<List<Float>> hasFloatList(String key) {
        return Optional.ofNullable(document.getList(key, Float.class));
    }

    /**
     * Will get a double list from the document.
     *
     * @param key the key
     * @return the double list
     */
    public Optional<List<Double>> hasDoubleList(String key) {
        return Optional.ofNullable(document.getList(key, Double.class));
    }

    /**
     * Will get an integer list from the document.
     *
     * @param key the key
     * @return the integer list
     */
    public Optional<List<Integer>> hasIntegerList(String key) {
        return Optional.ofNullable(document.getList(key, Integer.class));
    }

    /**
     * Will get a short list from the document.
     *
     * @param key the key
     * @return the short list
     */
    public Optional<List<Short>> hasShortList(String key) {
        return Optional.ofNullable(document.getList(key, Short.class));
    }

    /**
     * Will get a byte list from the document.
     *
     * @param key the key
     * @return the byte list
     */
    public Optional<List<Byte>> hasByteList(String key) {
        return Optional.ofNullable(document.getList(key, Byte.class));
    }

    /**
     * Will get a string list from the document.
     *
     * @param key the key
     * @return the string list
     */
    public Optional<List<String>> hasStringList(String key) {
        return Optional.ofNullable(document.getList(key, String.class));
    }

    /**
     * Will get a character list from the document.
     *
     * @param key the key
     * @return the character list
     */
    public Optional<List<Character>> hasCharacterList(String key) {
        return Optional.ofNullable(document.getList(key, Character.class));
    }

    /**
     * Will get a boolean list from the document.
     *
     * @param key the key
     * @return the boolean list
     */
    public Optional<List<Boolean>> hasBooleanList(String key) {
        return Optional.ofNullable(document.getList(key, Boolean.class));
    }

    /**
     * Will get a byte array from the document.
     * It's expected that the byte array was stored
     * through {@link #serializeByteArray(byte[], String)}
     *
     * @param key the key
     * @return the byte array
     */
    public byte @Nullable [] getByteArray(String key) {
        try {
            Binary byteArray = (Binary) document.get(key);
            return byteArray.getData();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Will serliaize a byte array into the document
     *
     * @param byteArray the byte array
     * @param key       the key
     */
    void serializeByteArray(byte[] byteArray, String key) {
        document.put(key, new Binary(byteArray));
    }

    /**
     * Will serialize the document into a byte array
     * which can later be deserialized through
     * {@link #deserialize(byte[])}
     *
     * @return the byte array
     */
    public byte @NotNull [] serialize() {
        return document.toJson().getBytes();
    }
}
