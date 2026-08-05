package io.github.anjoismysign.bloblib.storage;
import io.github.anjoismysign.bloblib.domain.PlayerDecorator;
import io.github.anjoismysign.bloblib.domain.PlayerDecoratorAware;
import io.github.anjoismysign.bloblib.profile.ProfiledAccount;

import io.github.anjoismysign.psa.crud.Crudable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AccountCrudable<T extends Crudable> implements ProfiledAccount<T>, PlayerDecoratorAware {

    private final String identification;
    private final List<T> profiles;
    private int currentProfileIndex;
    private transient PlayerDecorator playerDecorator;

    public AccountCrudable(String identification){
        this.identification = identification;
        this.profiles = new ArrayList<>();
        this.currentProfileIndex = 0;
        this.playerDecorator = null;
    }

    @Override
    public List<T> getProfiles() {
        return profiles;
    }

    @Override
    public int getCurrentProfileIndex() {
        return currentProfileIndex;
    }

    @Override
    public void setCurrentProfileIndex(int index) {
        currentProfileIndex = index;
    }

    @Override
    public @NotNull String getIdentification() {
        return identification;
    }

    @Override
    public void setPlayerDecorator(@NotNull PlayerDecorator playerDecorator) {
        this.playerDecorator = playerDecorator;
    }

    public PlayerDecorator getPlayerDecorator(){
        return playerDecorator;
    }

    @Nullable
    public T getCurrentProfile(){
        try {
            return profiles.get(currentProfileIndex);
        } catch (IndexOutOfBoundsException exception){
            return null;
        }
    }
}
