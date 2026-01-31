package io.github.anjoismysign.bloblib.entities;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ChunkedAccountCrudable implements ProfiledAccount<ProfileView>, PlayerDecoratorAware {

    private final String identification;
    private final List<ProfileView> profiles;
    private int currentProfileIndex;
    private transient PlayerDecorator playerDecorator;

    public ChunkedAccountCrudable(String identification){
        this.identification = identification;
        this.profiles = new ArrayList<>();
        this.currentProfileIndex = 0;
        this.playerDecorator = null;
    }

    @Override
    public List<ProfileView> getProfiles() {
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

    public PlayerDecorator getPlayerDecorator() {
        return playerDecorator;
    }
}
