package us.mytheria.bloblib.displayentity;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record DisplayFloatingPetSettings(EntityAnimationsCarrier animationsCarrier,
                                         DisplayMeasurements displayMeasurements) {
    private static final DisplayFloatingPetSettings DEFAULT = new DisplayFloatingPetSettings(
            EntityAnimationsCarrier.DEFAULT(), DisplayMeasurements.DEFAULT());

    public static DisplayFloatingPetSettings DEFAULT() {
        return DEFAULT;
    }

    /**
     * Will read from a ConfigurationSection.
     *
     * @param section The ConfigurationSection to read from.
     * @return The DisplayFloatingPetSettings read from the ConfigurationSection.
     */
    @NotNull
    public static DisplayFloatingPetSettings READ_OR_FAIL_FAST(ConfigurationSection section) {
        EntityAnimationsCarrier animationsCarrier;
        ConfigurationSection animations = section.getConfigurationSection("Animations");
        if (animations == null)
            animationsCarrier = EntityAnimationsCarrier.DEFAULT();
        else
            animationsCarrier = EntityAnimationsCarrier.READ_OR_FAIL_FAST(animations);
        DisplayMeasurements displayMeasurements;
        ConfigurationSection measurements = section.getConfigurationSection("Measurements");
        if (measurements == null)
            displayMeasurements = DisplayMeasurements.DEFAULT();
        else
            displayMeasurements = DisplayMeasurements.READ_OR_FAIL_FAST(measurements);
        return new DisplayFloatingPetSettings(animationsCarrier, displayMeasurements);
    }

    /**
     * Will read from a ConfigurationSection. If any exception is found, will
     * return null.
     *
     * @param section The ConfigurationSection to read from.
     * @return The DisplayFloatingPetSettings read from the ConfigurationSection.
     * Null if any exception is found.
     */
    @Nullable
    public static DisplayFloatingPetSettings READ_OR_NULL(ConfigurationSection section) {
        DisplayFloatingPetSettings settings;
        try {
            settings = READ_OR_FAIL_FAST(section);
            return settings;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Will serialize to a ConfigurationSection.
     *
     * @param section The ConfigurationSection to serialize to.
     */
    public void serialize(ConfigurationSection section) {
        animationsCarrier.serialize(section.createSection("Animations"));
        displayMeasurements.serialize(section.createSection("Measurements"));
    }
}
