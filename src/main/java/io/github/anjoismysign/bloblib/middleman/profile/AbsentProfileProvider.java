package io.github.anjoismysign.bloblib.middleman.profile;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AbsentProfileProvider implements ProfileProvider{
    @Override
    public @NotNull ProfileManagement getProfileManagement(@NotNull Player player) {
        throw new RuntimeException("No ProfileProvider has been detected");
    }

    @Override
    public boolean supportsSaving() {
        return false;
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
