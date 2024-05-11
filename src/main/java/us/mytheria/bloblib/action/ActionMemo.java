package us.mytheria.bloblib.action;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record ActionMemo(@NotNull String getReference,
                         @Nullable ActionType getActionType) {
    public static ActionMemo of(@NotNull String reference,
                                @NotNull ActionType actionType) {
        Objects.requireNonNull(reference, "'reference' cannot be null");
        Objects.requireNonNull(actionType, "'actionType' cannot be null");
        return new ActionMemo(reference, actionType);
    }

    @Nullable
    public Action<Entity> getAction() {
        if (getActionType() == null)
            return null;
        return switch (getActionType()) {
            case ACTOR_COMMAND -> CommandAction.build(getReference);
            case CONSOLE_COMMAND -> ConsoleCommandAction.build(getReference);
            default -> throw new IllegalStateException("Unexpected value: " + getActionType());
        };
    }
}
