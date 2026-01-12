package io.github.anjoismysign.bloblib.middleman.profile;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ProfileManagement {

    /**
     * Gets the Profiles this ProfileManagement has
     *
     * @return the Profiles
     */
    @NotNull List<Profile> getProfiles();

    /**
     * Gets the index of the Profile that is currently in use
     * @return the profile index
     */
    int getCurrentProfileIndex();

    /**
     * Attempts to switch to another Profile by its index
     * @param index The index of the Profile that will switch into
     */
    void switchProfile(int index);

}
