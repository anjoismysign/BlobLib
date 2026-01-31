package io.github.anjoismysign.bloblib.entities;

import org.jetbrains.annotations.NotNull;

public interface PlayerDecoratorAware {
    void setPlayerDecorator(@NotNull PlayerDecorator playerDecorator);
}
