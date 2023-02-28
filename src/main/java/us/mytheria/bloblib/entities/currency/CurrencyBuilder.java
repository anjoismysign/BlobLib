package us.mytheria.bloblib.entities.currency;

import org.bukkit.entity.Player;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.ObjectDirector;
import us.mytheria.bloblib.entities.inventory.ObjectBuilder;
import us.mytheria.bloblib.entities.inventory.ObjectBuilderButton;
import us.mytheria.bloblib.entities.inventory.ObjectBuilderButtonBuilder;
import us.mytheria.bloblib.entities.inventory.SharableInventory;
import us.mytheria.bloblib.entities.message.BlobSound;

import java.util.UUID;

public class CurrencyBuilder extends ObjectBuilder<Currency> {
    /*
    When the attribute is of boolean type, using a VariableSelector is stupid
    because it's a two state variable, so a switch is used
    that is called by interacting (lets say by clicking through InventoryClickEvent).
    */

    public static CurrencyBuilder build(UUID builderId,
                                        ObjectDirector<Currency> objectDirector) {
        return new CurrencyBuilder(
                BlobLibAssetAPI.buildInventory("CurrencyBuilder"), builderId,
                objectDirector);
    }

    private CurrencyBuilder(SharableInventory blobInventory, UUID builderId,
                            ObjectDirector<Currency> objectDirector) {
        super(blobInventory, builderId, objectDirector);
        ObjectBuilderButton<String> keyButton = ObjectBuilderButtonBuilder.QUICK_STRING(
                "Key", 300, this);
        ObjectBuilderButton<String> displayButton = ObjectBuilderButtonBuilder.QUICK_STRING(
                "Display", 300, this);
        ObjectBuilderButton<String> pattern = ObjectBuilderButtonBuilder.QUICK_STRING(
                "DecimalFormat", 300, this);
        ObjectBuilderButton<Double> initialBalance = ObjectBuilderButtonBuilder.POSITIVE_DOUBLE(
                "InitialBalance", 300, this);
        addObjectBuilderButton(displayButton)
                .addObjectBuilderButton(displayButton)
                .addObjectBuilderButton(pattern)
                .addObjectBuilderButton(initialBalance)
                .setFunction(builder -> {
                    Currency build = builder.construct();
                    if (build == null)
                        return null;
                    Player player = getPlayer();
                    BlobSound sound = BlobLibAssetAPI.getSound("Builder.Build-Complete");
                    sound.play(player);
                    player.closeInventory();
                    build.saveToFile(objectDirector.getObjectManager().getLoadFilesDirectory());
                    objectDirector.getObjectManager().addObject(build.getKey(), build);
                    objectDirector.getBuilderManager().removeBuilder(player);
                    return build;
                });

    }

    @SuppressWarnings("unchecked")
    @Override
    public Currency construct() {
        ObjectBuilderButton<String> keyButton = (ObjectBuilderButton<String>) getObjectBuilderButton("Key");
        ObjectBuilderButton<String> displayButton = (ObjectBuilderButton<String>) getObjectBuilderButton("Display");
        ObjectBuilderButton<String> pattern = (ObjectBuilderButton<String>) getObjectBuilderButton("DecimalFormat");
        ObjectBuilderButton<Double> initialBalance = (ObjectBuilderButton<Double>) getObjectBuilderButton("InitialBalance");

        if (keyButton.get().isEmpty() || displayButton.get().isEmpty() || pattern.get().isEmpty() || initialBalance.get().isEmpty())
            return null;

        String key = keyButton.get().get();
        String display = displayButton.get().get();
        String decimalFormatPattern = pattern.get().get();
        double initialBalanceValue = initialBalance.get().get();

        return new Currency(display, initialBalanceValue, true, decimalFormatPattern, key);
    }
}