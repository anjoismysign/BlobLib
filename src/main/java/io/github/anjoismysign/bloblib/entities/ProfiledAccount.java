package io.github.anjoismysign.bloblib.entities;

import io.github.anjoismysign.psa.crud.Crudable;

import java.util.List;

/**
 * A base interface for accounts that manage a list of profiles.
 * @param <P> The type of profile (e.g., T or ProfileView)
 */
public interface ProfiledAccount<P> extends Crudable {

    List<P> getProfiles();

    int getCurrentProfileIndex();

    void setCurrentProfileIndex(int index);
}