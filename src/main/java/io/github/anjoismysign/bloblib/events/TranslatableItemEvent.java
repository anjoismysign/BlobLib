package io.github.anjoismysign.bloblib.events;

import io.github.anjoismysign.bloblib.entities.translatable.TranslatableItem;
import org.bukkit.event.Event;

public abstract class TranslatableItemEvent extends Event {
    public TranslatableItemEvent(TranslatableItem translatableItem, boolean isAsync) {
        super(isAsync);
        this.translatableItem = translatableItem;
    }

    private final TranslatableItem translatableItem;

    public TranslatableItem getTranslatableItem() {
        return translatableItem;
    }
}
