package us.mytheria.bloblib.entities;

import java.util.Map;

/**
 * Will be used to represent a profile that can be inside
 * a SharedSerializable object (an object that can be
 * owned by multiple players).
 */
public interface SerializableProfile {
    /**
     * Will serialize the profile into a Map.
     *
     * @return the serialized profile
     */
    default Map<String, Object> serialize() {
        return Map.of(
                "ProfileName", getProfileName(),
                "LastKnownName", getLastKnownName(),
                "UniqueId", getIdentification()
        );
    }


    /**
     * May be randomly generated from a text file.
     * It is used to identify the profile in case
     * the infrastructure allows having multiple profiles
     *
     * @return the profile name
     */
    String getProfileName();

    /**
     * Will store the last known name of the player.
     * This must be retrieved when the player joins the server.
     *
     * @return the last known name of the player
     */
    String getLastKnownName();

    /**
     * The identification of the player.
     * This is used to identify the player in case
     * player changes their name.
     *
     * @return the identification of the player
     */
    String getIdentification();
}
