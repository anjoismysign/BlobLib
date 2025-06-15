package io.github.anjoismysign.bloblib.entities;

import org.bukkit.configuration.ConfigurationSection;

public record TinyEventListener(boolean register) {

    public static TinyEventListener TRUE() {
        return new TinyEventListener(true);
    }

    public static TinyEventListener FALSE() {
        return new TinyEventListener(false);
    }

    /**
     * Will read a TinyEventListener from a ConfigurationSection
     * Must point to a boolean
     *
     * @param section The ConfigurationSection to read from
     * @param name    The name of the TinyEventListener
     * @return The TinyEventListener
     * @throws RuntimeException If the ConfigurationSection does not point to a boolean
     */
    public static TinyEventListener READ(ConfigurationSection section, String name) {
        if (!section.isBoolean(name))
            throw new RuntimeException("Not a TinyEventListener");
        boolean register = section.getBoolean(name);
        return new TinyEventListener(register);
    }

    /**
     * If the TinyEventListener is registered, the consumer will be called
     *
     * @param runnable The runnable to run
     * @return true if the TinyEventListener is registered, false otherwise
     */
    public boolean ifRegister(Runnable runnable) {
        if (register) {
            runnable.run();
            return true;
        }
        return false;
    }

    /**
     * If the TinyEventListener is not registered, the consumer will be called
     *
     * @param runnable The runnable to run
     * @return true if the TinyEventListener is not registered, false otherwise
     */
    public boolean ifNotRegistered(Runnable runnable) {
        if (!register) {
            runnable.run();
            return true;
        }
        return false;
    }
}