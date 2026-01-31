package io.github.anjoismysign.bloblib.utilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public record ClassHandler<T> (Class<T> clazz) {

    public boolean isAssignable(Class<?> targetType){
        return targetType.isAssignableFrom(clazz);
    }

    public T constructCrudable(String identification) {
        T newInstance;
        try {
            Constructor<T> constructor = clazz.getConstructor(String.class);
            newInstance = constructor.newInstance(identification);
        } catch (NoSuchMethodException exception) {
            throw new RuntimeException(
                    "Class " + clazz.getName() +
                            " must have a public constructor that accepts a single String (identification) parameter. ",
                    exception
            );
        } catch (InstantiationException exception) {
            throw new RuntimeException(
                    "Failed to instantiate class " + clazz.getName() +
                            ". The class may be abstract or an interface.",
                    exception
            );
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(
                    "Cannot access the constructor of class " + clazz.getName() +
                            ". Make sure the constructor is public.",
                    exception
            );
        } catch (InvocationTargetException exception) {
            throw new RuntimeException(
                    "Constructor of class " + clazz.getName() +
                            " threw an exception during instantiation with identification: " + identification,
                    exception.getCause()
            );
        }
        return newInstance;
    }
}
