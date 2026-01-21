package io.github.anjoismysign.bloblib.middleman;

import io.github.anjoismysign.bloblib.middleman.profile.Profile;
import io.github.anjoismysign.bloblib.middleman.profile.ProfileManagement;
import io.github.anjoismysign.bloblib.middleman.profile.ProfileProvider;
import io.github.anjoismysign.blobtycoon.BlobTycoonInternalAPI;
import io.github.anjoismysign.blobtycoon.entity.PlotProfileView;
import io.github.anjoismysign.blobtycoon.entity.TycoonPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlobTycoonMiddleman implements ProfileProvider {

    private record BTProfile(PlotProfileView view) implements Profile{

        @Override
        public @NotNull String getIdentification() {
            return view.getIdentification();
        }

        @Override
        public @NotNull String getName() {
            return view.getName();
        }

        @Override
        public void save() {
            throw new RuntimeException("Do ProfileProvider#supportsSaving");
        }

        @Override
        public boolean hasPlayedBefore() {
            return view.hasPlayedBefore();
        }
    }

    private record BTProfileManagement(TycoonPlayer tycoonPlayer) implements ProfileManagement {

        @Override
        public @NotNull List<Profile> getProfiles() {
            List<Profile> list = new ArrayList<>();
            tycoonPlayer.getPlotProfileViews().forEach(view -> list.add(new BTProfile(view)));
            return list;
        }

        @Override
        public int getCurrentProfileIndex() {
            return tycoonPlayer.getSelectedProfile();
        }

        @Override
        public void switchProfile(int index) {
            tycoonPlayer.switchProfile(index, null, false);
        }
    }

    @Nullable
    private TycoonPlayer tycoonPlayer(@NotNull Player player){
        return BlobTycoonInternalAPI.getInstance().getTycoonPlayer(player);
    }

    @Override
    public @NotNull ProfileManagement getProfileManagement(@NotNull Player player) {
        return new BTProfileManagement(tycoonPlayer(player));
    }

    @Override
    public boolean supportsSaving() {
        return false;
    }

    @Override
    public boolean isValid(){
        return true;
    }

    @Override
    public @NotNull String getProviderName() {
        return "BlobTycoon";
    }

}
