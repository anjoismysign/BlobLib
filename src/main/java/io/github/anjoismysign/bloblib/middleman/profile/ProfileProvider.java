package io.github.anjoismysign.bloblib.middleman.profile;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ProfileProvider {

    /**
     * Gets the ProfileManagement of a Player.
     * Fails fast.
     * @param player The player to get the ProfileManagement
     * @return The ProfileManagement
     */
    @NotNull
    ProfileManagement getProfileManagement(@NotNull Player player);

    /**
     * If this ProfileProvider supports saving Profiles through Profile#save
     * @return true if supported, false otherwise
     */
    boolean supportsSaving();

    /**
     * If ProfileProvider is valid, which means if it has been detected.
     * @return true if valid, false otherwise
     */
    boolean isValid();

    /**
     * Gets the plugin provider name.
     * @return The plugin name.
     */
    @NotNull
    String getProviderName();

}
