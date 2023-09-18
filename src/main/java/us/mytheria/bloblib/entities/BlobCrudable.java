package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.crud.Crudable;
import org.bson.Document;

import java.util.List;
import java.util.Optional;

public final class BlobCrudable implements Crudable {
    private final String id;
    private final Document document;

    public BlobCrudable(String id, Document document) {
        this.id = id;
        this.document = document;
    }

    public BlobCrudable(String uuid) {
        this(uuid, new Document());
    }

    @Override
    public String getIdentification() {
        return id;
    }

    public Document getDocument() {
        return document;
    }

    public Optional<Object> has(String key) {
        return Optional.ofNullable(document.get(key));
    }

    public Optional<Long> hasLong(String key) {
        return Optional.ofNullable(document.getLong(key));
    }

    public Optional<Float> hasFloat(String key) {
        Float value = document.get(key, Float.class);
        if (value == null)
            return Optional.empty();
        return Optional.ofNullable(value);
    }

    public Optional<Double> hasDouble(String key) {
        return Optional.ofNullable(document.getDouble(key));
    }

    public Optional<Integer> hasInteger(String key) {
        return Optional.ofNullable(document.getInteger(key));
    }

    public Optional<Short> hasShort(String key) {
        Short value = document.get(key, Short.class);
        if (value == null)
            return Optional.empty();
        return Optional.ofNullable(value);
    }

    public Optional<Byte> hasByte(String key) {
        Byte value = document.get(key, Byte.class);
        if (value == null)
            return Optional.empty();
        return Optional.ofNullable(value);
    }

    public Optional<String> hasString(String key) {
        return Optional.ofNullable(document.getString(key));
    }

    public Optional<Character> hasCharacter(String key) {
        Character value = document.get(key, Character.class);
        if (value == null)
            return Optional.empty();
        return Optional.ofNullable(value);
    }

    public Optional<Boolean> hasBoolean(String key) {
        return Optional.ofNullable(document.getBoolean(key));
    }

    public Optional<List<Long>> hasLongList(String key) {
        return Optional.ofNullable(document.getList(key, Long.class));
    }

    public Optional<List<Float>> hasFloatList(String key) {
        return Optional.ofNullable(document.getList(key, Float.class));
    }

    public Optional<List<Double>> hasDoubleList(String key) {
        return Optional.ofNullable(document.getList(key, Double.class));
    }

    public Optional<List<Integer>> hasIntegerList(String key) {
        return Optional.ofNullable(document.getList(key, Integer.class));
    }

    public Optional<List<Short>> hasShortList(String key) {
        return Optional.ofNullable(document.getList(key, Short.class));
    }

    public Optional<List<Byte>> hasByteList(String key) {
        return Optional.ofNullable(document.getList(key, Byte.class));
    }

    public Optional<List<String>> hasStringList(String key) {
        return Optional.ofNullable(document.getList(key, String.class));
    }

    public Optional<List<Character>> hasCharacterList(String key) {
        return Optional.ofNullable(document.getList(key, Character.class));
    }

    public Optional<List<Boolean>> hasBooleanList(String key) {
        return Optional.ofNullable(document.getList(key, Boolean.class));
    }
}
