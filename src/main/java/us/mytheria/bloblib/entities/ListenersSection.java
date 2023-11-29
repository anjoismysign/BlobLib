package us.mytheria.bloblib.entities;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.exception.ConfigurationFieldException;

import java.util.List;

/**
 * Represents the listeners section of the configuration.
 * The methods for SimpleListeners offer using a custom value name
 * or either using the name of the section as the value name.
 *
 * @param tinyListeners    Tiny listeners section.
 * @param complexListeners Complex listeners section.
 * @param simpleListeners  Simple listeners section.
 * @param root             Root configuration section.
 */
public record ListenersSection(ConfigurationSection tinyListeners,
                               ConfigurationSection complexListeners,
                               ConfigurationSection simpleListeners,
                               FileConfiguration root) {

    /**
     * Will create a ListenersSection from the configuration of the plugin.
     *
     * @param plugin Plugin to get the configuration from.
     * @return ListenersSection.
     */
    @NotNull
    public static ListenersSection of(JavaPlugin plugin) {
        FileConfiguration configuration = plugin.getConfig();
        ConfigurationSection listeners = configuration.getConfigurationSection("Listeners");
        if (!listeners.isConfigurationSection("TinyListeners"))
            listeners.createSection("TinyListeners");
        if (!listeners.isConfigurationSection("ComplexListeners"))
            listeners.createSection("ComplexListeners");
        if (!listeners.isConfigurationSection("SimpleListeners"))
            listeners.createSection("SimpleListeners");
        ConfigurationSection tinyListeners = listeners.getConfigurationSection("TinyListeners");
        ConfigurationSection complexListeners = listeners.getConfigurationSection("ComplexListeners");
        ConfigurationSection simpleListeners = listeners.getConfigurationSection("SimpleListeners");
        return new ListenersSection(tinyListeners, complexListeners, simpleListeners, configuration);
    }

    /**
     * Gets a tiny event listener from the tiny listeners section.
     *
     * @param name Name of the tiny event listener.
     * @return Tiny event listener.
     */
    public TinyEventListener tinyEventListener(String name) {
        return TinyEventListener.READ(tinyListeners, name);
    }

    /**
     * Gets a complex event listener from the complex listeners section.
     *
     * @param name Name of the complex event listener.
     * @return Complex event listener.
     */
    public ComplexEventListener complexEventListener(String name) {
        ConfigurationSection section = complexListeners.getConfigurationSection(name);
        if (section == null)
            section = complexListeners.createSection(name);
        return new ComplexEventListener(section);
    }

    private ConfigurationSection getSimpleEventListenerSection(String name) {
        if (!simpleListeners.isConfigurationSection(name))
            throw new ConfigurationFieldException("'Listeners.SimpleListeners' is not a ConfigurationSection");
        return simpleListeners.getConfigurationSection(name);
    }

    public SimpleEventListener<String> simpleEventListenerString(String name, String valueName) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.STRING(section, valueName);
    }

    public SimpleEventListener<Integer> simpleEventListenerInteger(String name, String valueName) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.INTEGER(section, valueName);
    }

    public SimpleEventListener<Long> simpleEventListenerLong(String name, String valueName) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.LONG(section, valueName);
    }

    public SimpleEventListener<Double> simpleEventListenerDouble(String name, String valueName) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.DOUBLE(section, valueName);
    }

    public SimpleEventListener<Boolean> simpleEventListenerBoolean(String name, String valueName) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.BOOLEAN(section, valueName);
    }

    public SimpleEventListener<List<String>> simpleEventListenerStringList(String name, String valueName) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.STRING_LIST(section, valueName);
    }

    public SimpleEventListener<List<Byte>> simpleEventListenerByteList(String name, String valueName) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.BYTE_LIST(section, valueName);
    }

    public SimpleEventListener<List<Short>> simpleEventListenerShortList(String name, String valueName) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.SHORT_LIST(section, valueName);
    }

    public SimpleEventListener<List<Integer>> simpleEventListenerIntegerList(String name, String valueName) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.INTEGER_LIST(section, valueName);
    }

    public SimpleEventListener<List<Long>> simpleEventListenerLongList(String name, String valueName) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.LONG_LIST(section, valueName);
    }

    public SimpleEventListener<List<Float>> simpleEventListenerFloatList(String name, String valueName) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.FLOAT_LIST(section, valueName);
    }

    public SimpleEventListener<List<Double>> simpleEventListenerDoubleList(String name, String valueName) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.DOUBLE_LIST(section, valueName);
    }

    public SimpleEventListener<List<Boolean>> simpleEventListenerBooleanList(String name, String valueName) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.BOOLEAN_LIST(section, valueName);
    }

    public SimpleEventListener<List<Character>> simpleEventListenerCharacterList(String name, String valueName) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.CHARACTER_LIST(section, valueName);
    }

    public SimpleEventListener<String> simpleEventListenerString(String name) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.STRING(section, name);
    }

    public SimpleEventListener<Integer> simpleEventListenerInteger(String name) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.INTEGER(section, name);
    }

    public SimpleEventListener<Long> simpleEventListenerLong(String name) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.LONG(section, name);
    }

    public SimpleEventListener<Double> simpleEventListenerDouble(String name) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.DOUBLE(section, name);
    }

    public SimpleEventListener<Boolean> simpleEventListenerBoolean(String name) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.BOOLEAN(section, name);
    }

    public SimpleEventListener<List<String>> simpleEventListenerStringList(String name) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.STRING_LIST(section, name);
    }

    public SimpleEventListener<List<Byte>> simpleEventListenerByteList(String name) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.BYTE_LIST(section, name);
    }

    public SimpleEventListener<List<Short>> simpleEventListenerShortList(String name) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.SHORT_LIST(section, name);
    }

    public SimpleEventListener<List<Integer>> simpleEventListenerIntegerList(String name) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.INTEGER_LIST(section, name);
    }

    public SimpleEventListener<List<Long>> simpleEventListenerLongList(String name) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.LONG_LIST(section, name);
    }

    public SimpleEventListener<List<Float>> simpleEventListenerFloatList(String name) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.FLOAT_LIST(section, name);
    }

    public SimpleEventListener<List<Double>> simpleEventListenerDoubleList(String name) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.DOUBLE_LIST(section, name);
    }

    public SimpleEventListener<List<Boolean>> simpleEventListenerBooleanList(String name) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.BOOLEAN_LIST(section, name);
    }

    public SimpleEventListener<List<Character>> simpleEventListenerCharacterList(String name) {
        ConfigurationSection section = getSimpleEventListenerSection(name);
        return SimpleEventListener.CHARACTER_LIST(section, name);
    }
}
