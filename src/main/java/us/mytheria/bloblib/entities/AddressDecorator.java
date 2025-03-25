package us.mytheria.bloblib.entities;

import java.util.function.Consumer;

public interface AddressDecorator<T> {

    Address<T> address();

    default boolean isValid() {
        return address().look() != null;
    }

    default void ifValid(Consumer<T> consumer) {
        T lookup = address().look();
        if (lookup == null)
            return;
        consumer.accept(lookup);
    }
}
