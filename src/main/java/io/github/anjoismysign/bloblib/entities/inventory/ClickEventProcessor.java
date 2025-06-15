package io.github.anjoismysign.bloblib.entities.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class ClickEventProcessor implements ButtonVisitor {
    private final InventoryClickEvent event;
    private final InventoryDataRegistry<?> registry;

    public static ClickEventProcessor of(@NotNull InventoryClickEvent event,
                                         @NotNull InventoryDataRegistry<?> registry) {
        Objects.requireNonNull(event);
        Objects.requireNonNull(registry);
        return new ClickEventProcessor(event, registry);
    }

    private ClickEventProcessor(InventoryClickEvent event, InventoryDataRegistry<?> registry) {
        this.event = event;
        this.registry = registry;
    }

    @Override
    public void visit(InventoryButton button) {
        ((InventoryDataRegistry<InventoryButton>) registry).processClickEvent(event, button);
    }

    @Override
    public void visit(MetaInventoryButton button) {
        ((InventoryDataRegistry<MetaInventoryButton>) registry).processClickEvent(event, button);
    }
}
