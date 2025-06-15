package io.github.anjoismysign.bloblib.displayentity;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record EntityAnimationsCarrier(double followSpeed,
                                      double walkAwaySpeed,
                                      double hoverSpeed,
                                      double hoverHeightCeiling,
                                      double hoverHeightFloor,
                                      double yOffset,
                                      double particlesOffset,
                                      double teleportDistanceThreshold,
                                      double approachDistanceThreshold,
                                      double minimumDistance) {
    private static final EntityAnimationsCarrier DEFAULT_ANIMATIONS_CARRIER = new EntityAnimationsCarrier(
            0.35,
            0.2,
            0.03,
            0.2,
            -0.3,
            1.75,
            -1.25,
            5.0,
            2.5,
            1.0
    );

    /**
     * Will return the default EntityAnimationsCarrier.
     * Follow-Speed: 0.225
     * Walk-Away-Speed: 0.2
     * Hover-Speed: 0.03
     * Hover-Height-Ceiling: 0.2
     * Hover-Height-Floor: -0.3
     * Y-Offset: 1.5
     * Particles-Offset: -1.25
     * Teleport-Distance-Threshold: 5.0
     * Approach-Distance-Threshold: 2.5
     * Minimum-Distance: 1.0
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
                section.getDouble("Follow-Speed", 0.225),
                section.getDouble("Walk-Away-Speed", 0.2),
                section.getDouble("Hover-Speed", 0.03),
                section.getDouble("Hover-Height-Ceiling", 0.2),
                section.getDouble("Hover-Height-Floor", -0.3),
                section.getDouble("Y-Offset", 1.5),
                section.getDouble("Particles-Offset", -1.25),
                section.getDouble("Teleport-Distance-Threshold", 5.0),
                section.getDouble("Approach-Distance-Threshold", 2.5),
                section.getDouble("Minimum-Distance", 1.0));
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
    public void serialize(ConfigurationSection section) {
        section.set("Hover-Speed", hoverSpeed);
        section.set("Hover-Height-Ceiling", hoverHeightCeiling);
        section.set("Hover-Height-Floor", hoverHeightFloor);
        section.set("Y-Offset", yOffset);
        section.set("Particles-Offset", particlesOffset);
    }
}
