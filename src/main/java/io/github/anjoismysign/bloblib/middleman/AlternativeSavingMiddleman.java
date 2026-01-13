package io.github.anjoismysign.bloblib.middleman;

import io.github.anjoismysign.alternativesaving.director.manager.AlternativeSavingManager;
import io.github.anjoismysign.alternativesaving.entity.SerialPlayer;
import io.github.anjoismysign.alternativesaving.entity.SerialProfile;
import io.github.anjoismysign.bloblib.middleman.profile.Profile;
import io.github.anjoismysign.bloblib.middleman.profile.ProfileManagement;
import io.github.anjoismysign.bloblib.middleman.profile.ProfileProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AlternativeSavingMiddleman implements ProfileProvider {

    private record ASProfile(@NotNull SerialPlayer serialPlayer,
                             @NotNull SerialProfile serialProfile) implements Profile {

        @Override
        public @NotNull String getIdentification() {
            return serialPlayer.getIdentification()+":"+serialProfile.getIdentification();
        }

        @Override
        public @NotNull String getName() {
            return serialProfile.getProfileName();
        }

        @Override
        public void save() {
            if (serialPlayer.getProfiles().indexOf(serialProfile) != serialPlayer().getSelectedProfile()){
                return;
            }
            serialPlayer.saveCurrentProfile(Objects.requireNonNull(serialPlayer.getPlayer(), "Is SerialPlayer cached?"), serialProfile.hasPlayedBefore());
        }

        @Override
        public boolean hasPlayedBefore() {
            return serialProfile.hasPlayedBefore();
        }
    }

    private record ASProfileManagement(@NotNull SerialPlayer serialPlayer) implements ProfileManagement{

        @Override
        public @NotNull List<Profile> getProfiles() {
            List<Profile> list = new ArrayList<>();
            serialPlayer.getProfiles().forEach(serialProfile -> {
                list.add(new ASProfile(serialPlayer, serialProfile));
            });
            return list;
        }

        @Override
        public int getCurrentProfileIndex() {
            return serialPlayer.getSelectedProfile();
        }

        @Override
        public void switchProfile(int index) {
            serialPlayer.loadProfile(Objects.requireNonNull(serialPlayer.getPlayer(), "Is SerialPlayer cached?"), index);
        }
    }

    @Nullable
    private SerialPlayer serialPlayer(@NotNull Player player){
        return AlternativeSavingManager.getSerialPlayer(player);
    }


    @Override
    public @NotNull ProfileManagement getProfileManagement(@NotNull Player player) {
        return new ASProfileManagement(Objects.requireNonNull(serialPlayer(player), "Has Player cached a SerialPlayer?"));
    }

    @Override
    public boolean supportsSaving() {
        return true;
    }

    @Override
    public boolean isValid(){
        return true;
    }

}
