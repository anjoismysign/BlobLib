package io.github.anjoismysign.bloblib.listener;

import org.bukkit.event.Listener;

import java.lang.reflect.Method;

/**
 * A {@link Listener} that is registered or not based on a boolean property of a
 * configuration object, as handled by
 * {@link io.github.anjoismysign.bloblib.manager.SmartListenerManager}.
 * <p>
 * The property is named after the implementing class's simple name: a listener
 * class named {@code Foo} requires a public {@code isFoo()} or {@code getFoo()}
 * method returning {@code boolean} or {@link Boolean} on the configuration.
 * {@code null} is treated as {@code false}.
 * <p>
 * Plugins may declare a middle interface that extends this one so every listener
 * knows where its configuration lives while handling events (interfaces are not
 * scanned, so it is never registered itself):
 * <pre>{@code
 * public interface MyListener extends SmartTinyListener {
 *     default MyConfig getConfiguration() {
 *         return MyPlugin.getInstance().getListenerManager().getListenersConfiguration();
 *     }
 * }
 * }</pre>
 * Implementations must be concrete, have a public no-arg constructor and live
 * directly in the plugin's {@code listener} package to be scanned.
 */
public interface SmartTinyListener extends Listener {

    /**
     * Resolves the boolean getter named after {@code listenerClass}'s simple name
     * ({@code is<Name>()} or {@code get<Name>()}) on {@code configuration}.
     */
    static boolean shouldRegister(Object configuration, Class<?> listenerClass) {
        Class<?> configurationClass = configuration.getClass();
        String property = listenerClass.getSimpleName();

        for (String prefix : new String[]{"is", "get"}) {
            Method getter;
            try {
                getter = configurationClass.getMethod(prefix + property);
            } catch (NoSuchMethodException ignored) {
                continue;
            }
            Class<?> returnType = getter.getReturnType();
            if (returnType != boolean.class && returnType != Boolean.class) {
                continue;
            }
            try {
                return Boolean.TRUE.equals(getter.invoke(configuration));
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Failed to invoke " + getter.getName()
                        + " on " + configurationClass.getName(), exception);
            }
        }

        throw new IllegalStateException("No boolean getter 'is" + property + "()' or 'get" + property
                + "()' found in " + configurationClass.getName() + " for listener " + listenerClass.getName());
    }
}