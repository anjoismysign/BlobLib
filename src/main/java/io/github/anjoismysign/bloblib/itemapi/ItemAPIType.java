package io.github.anjoismysign.bloblib.itemapi;

import org.apache.commons.lang3.function.TriConsumer;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiFunction;

public enum ItemAPIType {
    DISCRIMINATOR(
            null,
            null,
            null),
    BOOLEAN(
            PersistentDataType.BOOLEAN,
            ((container, namespacedKey, value) -> {
                if (!(value instanceof Boolean b))
                    throw new RuntimeException("value is not Boolean instance");
                container.set(namespacedKey, PersistentDataType.BOOLEAN, b);
            }),
            (((container, namespacedKey) -> container.get(namespacedKey, PersistentDataType.BOOLEAN)))),
    BYTE(
            PersistentDataType.BYTE,
            ((container, namespacedKey, value) -> {
                if (!(value instanceof Byte b))
                    throw new RuntimeException("value is not Byte instance");
                container.set(namespacedKey, PersistentDataType.BYTE, b);
            }),
            (((container, namespacedKey) -> container.get(namespacedKey, PersistentDataType.BYTE)))
    ),
    SHORT(
            PersistentDataType.SHORT,
            ((container, namespacedKey, value) -> {
                if (!(value instanceof Short s))
                    throw new RuntimeException("value is not Short instance");
                container.set(namespacedKey, PersistentDataType.SHORT, s);
            }),
            (((container, namespacedKey) -> container.get(namespacedKey, PersistentDataType.SHORT)))
    ),
    INTEGER(
            PersistentDataType.INTEGER,
            ((container, namespacedKey, value) -> {
                if (!(value instanceof Integer integer))
                    throw new RuntimeException("value is not Integer instance");
                container.set(namespacedKey, PersistentDataType.INTEGER, integer);
            }),
            (((container, namespacedKey) -> container.get(namespacedKey, PersistentDataType.INTEGER)))
    ),
    LONG(
            PersistentDataType.LONG,
            ((container, namespacedKey, value) -> {
                if (!(value instanceof Long l))
                    throw new RuntimeException("value is not Long instance");
                container.set(namespacedKey, PersistentDataType.LONG, l);
            }),
            (((container, namespacedKey) -> container.get(namespacedKey, PersistentDataType.LONG)))
    ),
    FLOAT(
            PersistentDataType.FLOAT,
            ((container, namespacedKey, value) -> {
                if (!(value instanceof Float f))
                    throw new RuntimeException("value is not Float instance");
                container.set(namespacedKey, PersistentDataType.FLOAT, f);
            }),
            (((container, namespacedKey) -> container.get(namespacedKey, PersistentDataType.FLOAT)))
    ),
    DOUBLE(
            PersistentDataType.DOUBLE,
            ((container, namespacedKey, value) -> {
                if (!(value instanceof Double d))
                    throw new RuntimeException("value is not Double instance");
                container.set(namespacedKey, PersistentDataType.DOUBLE, d);
            }),
            (((container, namespacedKey) -> container.get(namespacedKey, PersistentDataType.DOUBLE)))
    ),
    STRING(
            PersistentDataType.STRING,
            ((container, namespacedKey, value) -> {
                if (!(value instanceof String string))
                    throw new RuntimeException("value is not String instance");
                container.set(namespacedKey, PersistentDataType.STRING, string);
            }),
            (((container, namespacedKey) -> container.get(namespacedKey, PersistentDataType.STRING)))
    );

    private final PersistentDataType<?, ?> persistentDataType;

    private final TriConsumer<PersistentDataContainer, NamespacedKey, Object> setter;

    private final BiFunction<PersistentDataContainer, NamespacedKey, Object> getter;

    private ItemAPIType(
            PersistentDataType<?, ?> persistentDataType,
            TriConsumer<PersistentDataContainer, NamespacedKey, Object> setter,
            BiFunction<PersistentDataContainer, NamespacedKey, Object> getter
    ) {
        this.persistentDataType = persistentDataType;
        this.setter = setter;
        this.getter = getter;
    }

    public boolean isDiscriminator() {
        return this == DISCRIMINATOR;
    }

    public PersistentDataType<?, ?> getPersistentDataType() {
        return persistentDataType;
    }

    public void set(
            @NotNull PersistentDataContainer container,
            @NotNull NamespacedKey namespacedKey,
            @NotNull Object value
    ) {
        Objects.requireNonNull(container, "'container' cannot be null");
        Objects.requireNonNull(namespacedKey, "'namespacedKey' cannot be null");
        Objects.requireNonNull(value, "'value' cannot be null");
        setter.accept(container, namespacedKey, value);
    }

    public boolean has(
            @NotNull PersistentDataContainer container,
            @NotNull NamespacedKey namespacedKey
    ) {
        Objects.requireNonNull(container, "'container' cannot be null");
        Objects.requireNonNull(namespacedKey, "'namespacedKey' cannot be null");
        return container.has(namespacedKey, persistentDataType);
    }

    public Object get(
            @NotNull PersistentDataContainer container,
            @NotNull NamespacedKey namespacedKey
    ) {
        Objects.requireNonNull(container, "'container' cannot be null");
        Objects.requireNonNull(namespacedKey, "'namespacedKey' cannot be null");
        return getter.apply(container, namespacedKey);
    }
}
