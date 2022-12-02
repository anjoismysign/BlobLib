package us.mytheria.bloblib.entities;

import java.util.Set;

public class SuperInventoryButton extends InventoryButton {
    private final String command;
    private final boolean executesCommand;

    public static SuperInventoryButton fromInventoryButton(InventoryButton button, String command,
                                                           boolean executesCommand) {
        return new SuperInventoryButton(button.getKey(), button.getSlots(), command,
                executesCommand);
    }

    public SuperInventoryButton(String key, Set<Integer> slots, String command,
                                boolean executesCommand) {
        super(key, slots);
        this.command = command;
        this.executesCommand = executesCommand;
    }

    public String getCommand() {
        return command;
    }
}
