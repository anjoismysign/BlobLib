package io.github.anjoismysign.bloblib.domain;

import org.jetbrains.annotations.NotNull;

public interface PlayerDecoratorAware {
    void setPlayerDecorator(@NotNull PlayerDecorator playerDecorator);
}
