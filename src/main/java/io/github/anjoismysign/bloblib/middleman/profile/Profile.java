package io.github.anjoismysign.bloblib.middleman.profile;

import io.github.anjoismysign.bloblib.entities.ProfileView;
import org.jetbrains.annotations.NotNull;

public interface Profile {

    /**
     * Gets this Profile's identification
     * @return the identification
     */
    @NotNull
    String getIdentification();

    /**
     * Gets this Profile's name
     * @return the name
     */
    @NotNull
    String getName();

    default ProfileView toView(){
        return new ProfileView(getIdentification(), getName());
    }

    /**
     * Saves this Profile to the database.
     */
    void save();

    boolean hasPlayedBefore();

}
