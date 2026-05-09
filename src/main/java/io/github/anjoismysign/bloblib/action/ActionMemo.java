package io.github.anjoismysign.bloblib.action;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ActionMemo(@NotNull String getReference,
                         @Nullable ActionType getActionType,
                         @NotNull String getPath) {

    @Nullable
    public Action<Entity> getAction() {
        if (getActionType() == null)
            return null;
        return switch (getActionType()) {
            case ACTOR_COMMAND -> CommandAction.build(getReference, getPath);
            case CONSOLE_COMMAND -> ConsoleCommandAction.build(getReference, getPath);
            default -> throw new IllegalStateException("Unexpected value: " + getActionType());
        };
    }
}
