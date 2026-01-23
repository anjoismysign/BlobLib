package io.github.anjoismysign.bloblib.middleman.profile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class AbsentProfileProvider implements ProfileProvider{
    @Override
    public @NotNull ProfileManagement getProfileManagement(@NotNull Player player) {
        return new ProfileManagement() {
            String identification = player.getUniqueId().toString();
            Profile profile = new Profile() {
                @Override
                public @NotNull String getIdentification() {
                    return identification;
                }

                @Override
                public @NotNull String getName() {
                    return "default";
                }

                @Override
                public void save() {
                }

                @Override
                public boolean hasPlayedBefore() {
                    return player.hasPlayedBefore();
                }
            };

            @Override
            public @NotNull List<Profile> getProfiles() {
                return List.of(profile);
            }

            @Override
            public int getCurrentProfileIndex() {
                return 0;
            }

            @Override
            public void switchProfile(int index) {
            }
        };
    }

    @Override
    public @Nullable ProfileManagement getProfileManagement(@NotNull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null){
            return null;
        }
        return new ProfileManagement() {
            String identification = uuid.toString();
            Profile profile = new Profile() {
                @Override
                public @NotNull String getIdentification() {
                    return identification;
                }

                @Override
                public @NotNull String getName() {
                    return "default";
                }

                @Override
                public void save() {
                }

                @Override
                public boolean hasPlayedBefore() {
                    return player.hasPlayedBefore();
                }
            };

            @Override
            public @NotNull List<Profile> getProfiles() {
                return List.of(profile);
            }

            @Override
            public int getCurrentProfileIndex() {
                return 0;
            }

            @Override
            public void switchProfile(int index) {
            }
        };
    }

    @Override
    public boolean supportsSaving() {
        return false;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public @NotNull String getProviderName() {
        return "AbsentProfileProvider";
    }
}
