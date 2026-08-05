package io.github.anjoismysign.bloblib.vault.profile;

import io.github.anjoismysign.bloblib.profile.ProfileView;
import net.milkbowl.vault.profile.Profile;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Allows a Vault {@link Profile} provider to be used without needing to check
 * if a profile provider is present, by providing an {@link #absent()} fallback
 * implementation that behaves like the old AbsentProfileProvider.
 */
public class ElasticProfile implements Profile {
    private final Profile profile;
    private final ElasticProfileType type;

    public static ElasticProfile of(Object object) {
        if (object instanceof Profile) {
            return of((Profile) object);
        }
        return absent();
    }

    public static ElasticProfile of(@NotNull Profile profile) {
        return new ElasticProfile(profile, ElasticProfileType.PRESENT);
    }

    public static ElasticProfile absent() {
        return new ElasticProfile(new Absent(), ElasticProfileType.ABSENT);
    }

    protected ElasticProfile(@NotNull Profile profile,
                             @NotNull ElasticProfileType type) {
        this.profile = profile;
        this.type = type;
    }

    @Override
    public boolean isEnabled() {
        return profile.isEnabled();
    }

    @Override
    public @NotNull String getName() {
        return profile.getName();
    }

    @Override
    public int getProfileCount(@NotNull OfflinePlayer player) {
        return profile.getProfileCount(player);
    }

    @Override
    public @NotNull String getProfileIdentification(@NotNull OfflinePlayer player, int index) {
        return profile.getProfileIdentification(player, index);
    }

    @Override
    public @NotNull String getProfileName(@NotNull OfflinePlayer player, int index) {
        return profile.getProfileName(player, index);
    }

    @Override
    public boolean hasProfilePlayedBefore(@NotNull OfflinePlayer player, int index) {
        return profile.hasProfilePlayedBefore(player, index);
    }

    @Override
    public int getCurrentProfileIndex(@NotNull OfflinePlayer player) {
        return profile.getCurrentProfileIndex(player);
    }

    @Override
    public boolean switchProfile(@NotNull OfflinePlayer player, int index) {
        return profile.switchProfile(player, index);
    }

    /**
     * If true, there is no Vault compatible profile provider.
     * This case (returning true), can only be given if there's no profile Vault
     * compatible plugin or if the plugin's developer/author didn't provide to Vault.
     *
     * @return true if there's no Vault profile provider
     */
    public boolean isAbsent() {
        return type == ElasticProfileType.ABSENT;
    }

    /**
     * If true, it means that a Vault compatible profile provider is present.
     *
     * @return true if a Vault profile provider is present
     */
    public boolean isPresent() {
        return type == ElasticProfileType.PRESENT;
    }

    public ElasticProfileType getType() {
        return type;
    }

    /**
     * Builds a {@link ProfileView} snapshot of the profile of a player at the
     * given index.
     *
     * @param player the owner of the profile
     * @param index  the index of the profile
     * @return the ProfileView
     */
    public @NotNull ProfileView toView(@NotNull OfflinePlayer player, int index) {
        return ProfileView.of(getProfileIdentification(player, index),
                getProfileName(player, index));
    }
}
