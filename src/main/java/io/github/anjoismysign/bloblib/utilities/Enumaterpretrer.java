package io.github.anjoismysign.bloblib.utilities;

import io.github.anjoismysign.bloblib.exception.InterpretationException;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Interpreter for enum values.
 */
public class Enumaterpretrer {

    /**
     * Decodes a set of enum values from an integer.
     *
     * @param intValue  The integer to decode.
     * @param enumClass The enum class to decode the values from.
     * @param <E>       The enum type.
     * @return A set of enum values.
     */
    public static <E extends Enum<E>> Set<E> decodeEnumSet(int intValue, Class<E> enumClass) {
        Set<E> flags = new HashSet<>();
        E[] enumConstants = enumClass.getEnumConstants();
        for (E constant : enumConstants) {
            int ordinalValue = 1 << constant.ordinal();
            if ((intValue & ordinalValue) == ordinalValue) {
                flags.add(constant);
            }
        }
        return flags;
    }

    /**
     * Encodes a set of enum values to an integer.
     *
     * @param enumSet The set of enum values to encode.
     * @param <E>     The enum type.
     * @return The integer value.
     */
    public static <E extends Enum<E>> int encodeEnumSet(Set<E> enumSet) {
        int intValue = 0;
        for (E constant : enumSet) {
            intValue |= 1 << constant.ordinal();
        }
        return intValue;
    }

    public static <E extends Enum<E>> Set<E> PARSE(Class<E> enumClass, String... strings) {
        return new Enumaterpretrer().parse(Stream.of(strings), enumClass);
    }

    public static <E extends Enum<E>> Set<E> PARSE(Class<E> enumClass, Collection<String> strings) {
        return new Enumaterpretrer().parse(strings.stream(), enumClass);
    }

    private enum ParseOperations {
        NOT_EQUALS(),
        STARTS_WITH(),
        ENDS_WITH(),
        CONTAINS(),
        NOT_STARTS_WITH(),
        NOT_ENDS_WITH(),
        NOT_CONTAINS()
    }

    private <E extends Enum<E>> Set<E> parse(Stream<String> stream, Class<E> enumClass) {
        EnumSet<E> allOf = EnumSet.allOf(enumClass);
        return stream.map(s -> parseOperation(s, enumClass))
                .flatMap(operation -> applyOperation(allOf, operation))
                .collect(Collectors.toSet());
    }

    private <E extends Enum<E>> Operation<E> parseOperation(String s, Class<E> enumClass) {
        if (s.contains("/")) {
            String[] split = s.split("/");
            if (split.length == 1)
                throw InterpretationException.INVALID_INPUT(s);
            try {
                ParseOperations operation = ParseOperations.valueOf(split[0]);
                String enumName = split[1];
                E enumValue = Enum.valueOf(enumClass, enumName);
                return new Operation<>(operation, enumValue);
            } catch (IllegalArgumentException e) {
                throw InterpretationException.INVALID_OPERATION(s);
            }
        } else {
            E enumValue = Enum.valueOf(enumClass, s);
            return new Operation<>(null, enumValue);
        }
    }

    private <E extends Enum<E>> Stream<E> applyOperation(EnumSet<E> allOf, Operation<E> operation) {
        if (operation.operation == null) {
            return Stream.of(operation.enumValue);
        }

        return allOf.stream().filter(enumValue -> {
            switch (operation.operation) {
                case NOT_EQUALS:
                    return enumValue != operation.enumValue;
                case STARTS_WITH:
                    return enumValue.name().startsWith(operation.enumValue.name());
                case ENDS_WITH:
                    return enumValue.name().endsWith(operation.enumValue.name());
                case CONTAINS:
                    return enumValue.name().contains(operation.enumValue.name());
                case NOT_STARTS_WITH:
                    return !enumValue.name().startsWith(operation.enumValue.name());
                case NOT_ENDS_WITH:
                    return !enumValue.name().endsWith(operation.enumValue.name());
                case NOT_CONTAINS:
                    return !enumValue.name().contains(operation.enumValue.name());
                default:
                    return false;
            }
        });
    }

    private static class Operation<E extends Enum<E>> {
        private final ParseOperations operation;
        private final E enumValue;

        public Operation(ParseOperations operation, E enumValue) {
            this.operation = operation;
            this.enumValue = enumValue;
        }
    }

}
