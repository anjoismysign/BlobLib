package us.mytheria.bloblib;

import org.bukkit.Material;
import us.mytheria.bloblib.entities.Fuel;
import us.mytheria.bloblib.itemstack.ItemStackBuilder;

import java.util.HashMap;
import java.util.Map;

public class FuelAPI {

    private static Map<String, Fuel> mapping = new HashMap<>();

    static {
        mapping.put(Material.LAVA_BUCKET.toString(), Fuel.withReplacement(ItemStackBuilder.build(Material.BUCKET).build(), 20000));
    }
}
