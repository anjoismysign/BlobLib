package io.github.anjoismysign.bloblib.component.textbubble;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface TextBubbleContainer {
    @Nullable
    TextBubbleComponent textBubble();

    default void ifTextBubble(@NotNull Consumer<TextBubbleComponent> consumer) {
        @Nullable TextBubbleComponent textBubble = textBubble();
        if (textBubble == null)
            return;
        consumer.accept(textBubble);
    }
}
