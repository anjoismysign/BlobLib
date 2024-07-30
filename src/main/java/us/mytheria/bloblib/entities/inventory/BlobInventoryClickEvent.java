package us.mytheria.bloblib.entities.inventory;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.api.BlobLibSoundAPI;
import us.mytheria.bloblib.entities.message.BlobSound;

import java.util.Objects;

public class BlobInventoryClickEvent extends InventoryClickEvent {
    @NotNull
    private String clickSound;
    private boolean playClickSound;

    public static BlobInventoryClickEvent of(@NotNull InventoryClickEvent event) {
        Objects.requireNonNull(event, "'event' cannot be null");
        return new BlobInventoryClickEvent(event.getView(), event.getSlotType(), event.getRawSlot(), event.getClick(), event.getAction());
    }

    private BlobInventoryClickEvent(@NotNull InventoryView view, @NotNull InventoryType.SlotType type, int slot, @NotNull ClickType click, @NotNull InventoryAction action) {
        super(view, type, slot, click, action);
        this.clickSound = "Builder.Button-Click";
        this.playClickSound = true;
    }

    public boolean playClickSound() {
        return playClickSound;
    }

    public void setPlayClickSound(boolean playClickSound) {
        this.playClickSound = playClickSound;
    }

    @NotNull
    public String getClickSound() {
        return clickSound;
    }

    public void setClickSound(@NotNull String clickSound) {
        Objects.requireNonNull(clickSound, "'clickSound' cannot be null");
        this.clickSound = clickSound;
    }

    @NotNull
    public BlobSound getClickBlobSound() {
        return Objects.requireNonNull(BlobLibSoundAPI.getInstance().getSound(getClickSound()), "Not a valid BlobSound: " + getClickSound());
    }
}
