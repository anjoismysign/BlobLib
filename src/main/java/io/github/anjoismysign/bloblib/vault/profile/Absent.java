package io.github.anjoismysign.bloblib.vault.profile;

import net.milkbowl.vault.profile.Profile;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Profile} implementation that provides a default profile
 * when there is no profile plugin compatible with Vault.
 */
public class Absent implements Profile {
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public @NotNull String getName() {
        return "AbsentProfileProvider";
    }

    @Override
    public int getProfileCount(@NotNull OfflinePlayer player) {
        return 1;
    }

    @Override
    public @NotNull String getProfileIdentification(@NotNull OfflinePlayer player, int index) {
        return player.getUniqueId().toString();
    }

    @Override
    public @NotNull String getProfileName(@NotNull OfflinePlayer player, int index) {
        return "default";
    }

    @Override
    public boolean hasProfilePlayedBefore(@NotNull OfflinePlayer player, int index) {
        return player.hasPlayedBefore();
    }

    @Override
    public int getCurrentProfileIndex(@NotNull OfflinePlayer player) {
        return 0;
    }

    @Override
    public boolean switchProfile(@NotNull OfflinePlayer player, int index) {
        return false;
    }
}
