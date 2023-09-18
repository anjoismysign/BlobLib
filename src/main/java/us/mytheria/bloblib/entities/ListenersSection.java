package us.mytheria.bloblib.entities;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * @param tinyListeners    Tiny listeners section.
 * @param complexListeners Complex listeners section.
 * @param simpleListeners  Simple listeners section.
 * @param root             Root configuration section.
 */
public record ListenersSection(ConfigurationSection tinyListeners,
                               ConfigurationSection complexListeners,
                               ConfigurationSection simpleListeners,
                               FileConfiguration root) {
    public TinyEventListener tinyEventListener(String name) {
        return TinyEventListener.READ(tinyListeners, name);
    }

    public ComplexEventListener complexEventListener(String name) {
        ConfigurationSection section = complexListeners.getConfigurationSection(name);
        if (section == null)
            section = complexListeners.createSection(name);
        return new ComplexEventListener(section);
    }

    public SimpleEventListener<String> simpleEventListenerString(String name) {
        return SimpleEventListener.STRING(simpleListeners, name);
    }

    public SimpleEventListener<Integer> simpleEventListenerInteger(String name) {
        return SimpleEventListener.INTEGER(simpleListeners, name);
    }

    public SimpleEventListener<Long> simpleEventListenerLong(String name) {
        return SimpleEventListener.LONG(simpleListeners, name);
    }

    public SimpleEventListener<Double> simpleEventListenerDouble(String name) {
        return SimpleEventListener.DOUBLE(simpleListeners, name);
    }

    public SimpleEventListener<Boolean> simpleEventListenerBoolean(String name) {
        return SimpleEventListener.BOOLEAN(simpleListeners, name);
    }

    public SimpleEventListener<List<String>> simpleEventListenerStringList(String name) {
        return SimpleEventListener.STRING_LIST(simpleListeners, name);
    }

    public SimpleEventListener<List<Byte>> simpleEventListenerByteList(String name) {
        return SimpleEventListener.BYTE_LIST(simpleListeners, name);
    }

    public SimpleEventListener<List<Short>> simpleEventListenerShortList(String name) {
        return SimpleEventListener.SHORT_LIST(simpleListeners, name);
    }

    public SimpleEventListener<List<Integer>> simpleEventListenerIntegerList(String name) {
        return SimpleEventListener.INTEGER_LIST(simpleListeners, name);
    }

    public SimpleEventListener<List<Long>> simpleEventListenerLongList(String name) {
        return SimpleEventListener.LONG_LIST(simpleListeners, name);
    }

    public SimpleEventListener<List<Float>> simpleEventListenerFloatList(String name) {
        return SimpleEventListener.FLOAT_LIST(simpleListeners, name);
    }

    public SimpleEventListener<List<Double>> simpleEventListenerDoubleList(String name) {
        return SimpleEventListener.DOUBLE_LIST(simpleListeners, name);
    }

    public SimpleEventListener<List<Boolean>> simpleEventListenerBooleanList(String name) {
        return SimpleEventListener.BOOLEAN_LIST(simpleListeners, name);
    }

    public SimpleEventListener<List<Character>> simpleEventListenerCharacterList(String name) {
        return SimpleEventListener.CHARACTER_LIST(simpleListeners, name);
    }
}
