package us.mytheria.bloblib.displayentity;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record EntityAnimationsCarrier(double hoverSpeed,
                                      double hoverHeightCeiling,
                                      double hoverHeightFloor,
                                      double yOffset,
                                      double particlesOffset) {
    private static final EntityAnimationsCarrier DEFAULT_ANIMATIONS_CARRIER = new EntityAnimationsCarrier(
            0.03,
            0.2,
            -0.3,
            1.75,
            -1.25
    );

    /**
     * Will return the default EntityAnimationsCarrier.
     * Hover-Speed: 0.03
     * Hover-Height-Ceiling: 0.2
     * Hover-Height-Floor: -0.3
     * Y-Offset: 1.75
     * Particles-Offset: -1.25
     *
     * @return The default EntityAnimationsCarrier.
     */
    public static EntityAnimationsCarrier DEFAULT() {
        return DEFAULT_ANIMATIONS_CARRIER;
    }

    /**
     * Will read from a ConfigurationSection.
     *
     * @param section The ConfigurationSection to read from.
     * @return The EntityAnimationsCarrier read from the ConfigurationSection.
     */
    @NotNull
    public static EntityAnimationsCarrier READ_OR_FAIL_FAST(ConfigurationSection section) {
        return new EntityAnimationsCarrier(
                section.getDouble("Hover-Speed", 0.03),
                section.getDouble("Hover-Height-Ceiling", 0.2),
                section.getDouble("Hover-Height-Floor", -0.3),
                section.getDouble("Y-Offset", 1.75),
                section.getDouble("Particles-Offset", -1.25));
    }

    /**
     * Will read from a ConfigurationSection. If any exception is found, will
     * return null.
     *
     * @param section The ConfigurationSection to read from.
     * @return The EntityAnimationsCarrier read from the ConfigurationSection.
     * Null if any exception is found.
     */
    @Nullable
    public static EntityAnimationsCarrier READ_OR_NULL(ConfigurationSection section) {
        EntityAnimationsCarrier carrier;
        try {
            carrier = READ_OR_FAIL_FAST(section);
            return carrier;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Serializes the EntityAnimationsCarrier to a ConfigurationSection.
     *
     * @param section The ConfigurationSection to serialize to.
     */
    public void serialize(ConfigurationSection section, String name) {
        ConfigurationSection animations = section.createSection(name);
        animations.set("Hover-Speed", hoverSpeed);
        animations.set("Hover-Height-Ceiling", hoverHeightCeiling);
        animations.set("Hover-Height-Floor", hoverHeightFloor);
        animations.set("Y-Offset", yOffset);
        animations.set("Particles-Offset", particlesOffset);
    }
}
