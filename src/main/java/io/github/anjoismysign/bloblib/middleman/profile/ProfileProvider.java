package io.github.anjoismysign.bloblib.middleman.profile;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

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
     * Gets the ProfileManager of a Player's uniqueId
     * @param uuid The uniqueId of the Player
     * @return The ProfileManagement if cached, null otherwise.
     */
    @Nullable
    ProfileManagement getProfileManagement(@NotNull UUID uuid);

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
