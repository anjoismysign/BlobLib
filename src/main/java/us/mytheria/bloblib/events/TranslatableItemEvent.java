package us.mytheria.bloblib.events;

import org.bukkit.event.Event;
import us.mytheria.bloblib.entities.translatable.TranslatableItem;

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
