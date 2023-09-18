package us.mytheria.bloblib;

import me.anjoismysign.anjo.entities.Result;
import org.bukkit.Material;
import us.mytheria.bloblib.entities.Fuel;
import us.mytheria.bloblib.itemstack.ItemStackBuilder;

import java.util.HashMap;
import java.util.Map;

/*
 * Provides Fuel objects for vanilla items.
 * Might want to check if item is custom.
 * If so, might want to apply your own
 * fuel time.
 */
public class FuelAPI {
    private static FuelAPI instance;

    private FuelAPI() {
        mapping.put("LAVA_BUCKET",
                Fuel.withReplacement(ItemStackBuilder.build(Material.BUCKET).build(),
                        20000));
        mapping.put("COAL_BLOCK", Fuel.of(16000));
        mapping.put("DRIED_KELP_BLOCK", Fuel.of(4000));
        mapping.put("BLAZE_ROD", Fuel.of(2400));
        mapping.put("COAL", Fuel.of(1600));
        mapping.put("CHARCOAL", Fuel.of(1600));
        for (String boatMaterial : new String[]{
                "OAK_BOAT",
                "BIRCH_BOAT",
                "SPRUCE_BOAT",
                "JUNGLE_BOAT",
                "ACACIA_BOAT",
                "DARK_OAK_BOAT",
                "MANGROVE_BOAT",
                "OAK_CHEST_BOAT",
                "BIRCH_CHEST_BOAT",
                "SPRUCE_CHEST_BOAT",
                "JUNGLE_CHEST_BOAT",
                "ACACIA_CHEST_BOAT",
                "DARK_OAK_CHEST_BOAT",
                "MANGROVE_CHEST_BOAT",
                "BAMBOO_RAFT",
                "BAMBOO_CHEST_RAFT",
                "CHERRY_BOAT",
                "CHERRY_CHEST_BOAT"
        }) {
            mapping.put(boatMaterial, Fuel.of(1200));
        }
        mapping.put(Material.SCAFFOLDING.toString(), Fuel.of(50));
        for (String hangingSign : new String[]{
                "ACACIA_HANGING_SIGN",
                "BAMBOO_HANGING_SIGN",
                "BIRCH_HANGING_SIGN",
                "DARK_OAK_HANGING_SIGN",
                "JUNGLE_HANGING_SIGN",
                "OAK_HANGING_SIGN",
                "SPRUCE_HANGING_SIGN",
                "MANGROVE_HANGING_SIGN",
                "CHERRY_HANGING_SIGN"}) {
            mapping.put(hangingSign.toString(), Fuel.of(200));
        }
        mapping.put(Material.BAMBOO_MOSAIC.toString(), Fuel.of(300));
        mapping.put("BEE_NEST", Fuel.of(300));
        mapping.put("BEEHIVE", Fuel.of(300));
        mapping.put("CHISELED_BOOKSHELF", Fuel.of(300));
        for (String woodMaterial : new String[]{
                "OAK_PLANKS",
                "BIRCH_PLANKS",
                "SPRUCE_PLANKS",
                "JUNGLE_PLANKS",
                "ACACIA_PLANKS",
                "DARK_OAK_PLANKS",
                "OAK_LOG",
                "BIRCH_LOG",
                "SPRUCE_LOG",
                "JUNGLE_LOG",
                "ACACIA_LOG",
                "DARK_OAK_LOG",
                "STRIPPED_OAK_LOG",
                "STRIPPED_BIRCH_LOG",
                "STRIPPED_SPRUCE_LOG",
                "STRIPPED_JUNGLE_LOG",
                "STRIPPED_ACACIA_LOG",
                "STRIPPED_DARK_OAK_LOG",
                "OAK_WOOD",
                "BIRCH_WOOD",
                "SPRUCE_WOOD",
                "JUNGLE_WOOD",
                "ACACIA_WOOD",
                "DARK_OAK_WOOD",
                "STRIPPED_OAK_WOOD",
                "STRIPPED_BIRCH_WOOD",
                "STRIPPED_SPRUCE_WOOD",
                "STRIPPED_JUNGLE_WOOD",
                "STRIPPED_ACACIA_WOOD",
                "STRIPPED_DARK_OAK_WOOD",
                "OAK_STAIRS",
                "BIRCH_STAIRS",
                "SPRUCE_STAIRS",
                "JUNGLE_STAIRS",
                "ACACIA_STAIRS",
                "DARK_OAK_STAIRS",
                "OAK_PRESSURE_PLATE",
                "BIRCH_PRESSURE_PLATE",
                "SPRUCE_PRESSURE_PLATE",
                "JUNGLE_PRESSURE_PLATE",
                "ACACIA_PRESSURE_PLATE",
                "DARK_OAK_PRESSURE_PLATE",
                "OAK_TRAPDOOR",
                "BIRCH_TRAPDOOR",
                "SPRUCE_TRAPDOOR",
                "JUNGLE_TRAPDOOR",
                "ACACIA_TRAPDOOR",
                "DARK_OAK_TRAPDOOR",
                "ACACIA_FENCE_GATE",
                "BIRCH_FENCE_GATE",
                "SPRUCE_FENCE_GATE",
                "JUNGLE_FENCE_GATE",
                "ACACIA_FENCE_GATE",
                "DARK_OAK_FENCE_GATE",
                "ACACIA_FENCE",
                "BIRCH_FENCE",
                "SPRUCE_FENCE",
                "JUNGLE_FENCE",
                "ACACIA_FENCE",
                "DARK_OAK_FENCE",
                "MANGROVE_PLANKS",
                "MANGROVE_LOG",
                "STRIPPED_MANGROVE_LOG",
                "MANGROVE_WOOD",
                "STRIPPED_MANGROVE_WOOD",
                "MANGROVE_STAIRS",
                "MANGROVE_PRESSURE_PLATE",
                "MANGROVE_TRAPDOOR",
                "MANGROVE_FENCE_GATE",
                "MANGROVE_FENCE",
                "BAMBOO_BLOCK",
                "STRIPPED_BAMBOO_BLOCK",
                "BAMBOO_STAIRS",
                "BAMBOO_PRESSURE_PLATE",
                "BAMBOO_TRAPDOOR",
                "BAMBOO_FENCE_GATE",
                "BAMBOO_FENCE",
                "CHERRY_PLANKS",
                "CHERRY_LOG",
                "STRIPPED_CHERRY_LOG",
                "CHERRY_WOOD",
                "STRIPPED_CHERRY_WOOD",
                "CHERRY_STAIRS",
                "CHERRY_PRESSURE_PLATE",
                "CHERRY_TRAPDOOR",
                "CHERRY_FENCE_GATE",
                "CHERRY_FENCE"
        }) {
            mapping.put(woodMaterial, Fuel.of(300));
        }
        mapping.put("MANGROVE_ROOTS", Fuel.of(300));
        mapping.put("LADDER", Fuel.of(300));
        mapping.put("CRAFTING_TABLE", Fuel.of(300));
        mapping.put("CARTOGRAPHY_TABLE", Fuel.of(300));
        mapping.put("FLETCHING_TABLE", Fuel.of(300));
        mapping.put("SMITHING_TABLE", Fuel.of(300));
        mapping.put("LOOM", Fuel.of(300));
        mapping.put("BOOKSHELF", Fuel.of(300));
        mapping.put("LECTERN", Fuel.of(300));
        mapping.put("COMPOSTER", Fuel.of(300));
        mapping.put("CHEST", Fuel.of(300));
        mapping.put("TRAPPED_CHEST", Fuel.of(300));
        mapping.put("BARREL", Fuel.of(300));
        mapping.put("DAYLIGHT_DETECTOR", Fuel.of(300));
        mapping.put("JUKEBOX", Fuel.of(300));
        mapping.put("NOTE_BLOCK", Fuel.of(300));
        mapping.put("FISHING_ROD", Fuel.of(300));
        for (String bannerMaterial : new String[]{
                "WHITE_BANNER",
                "ORANGE_BANNER",
                "MAGENTA_BANNER",
                "LIGHT_BLUE_BANNER",
                "YELLOW_BANNER",
                "LIME_BANNER",
                "PINK_BANNER",
                "GRAY_BANNER",
                "LIGHT_GRAY_BANNER",
                "CYAN_BANNER",
                "PURPLE_BANNER",
                "BLUE_BANNER",
                "BROWN_BANNER",
                "GREEN_BANNER",
                "RED_BANNER",
                "BLACK_BANNER",
        }) {
            mapping.put(bannerMaterial, Fuel.of(300));
        }
        for (String doorMaterial : new String[]{
                "OAK_DOOR",
                "BIRCH_DOOR",
                "SPRUCE_DOOR",
                "JUNGLE_DOOR",
                "ACACIA_DOOR",
                "DARK_OAK_DOOR",
        }) {
            mapping.put(doorMaterial, Fuel.of(200));
        }

        for (String sign : new String[]{
                "OAK_SIGN",
                "BIRCH_SIGN",
                "SPRUCE_SIGN",
                "JUNGLE_SIGN",
                "ACACIA_SIGN",
                "DARK_OAK_SIGN",
                "MANGROVE_SIGN",
                "BAMBOO_SIGN.toString()",
                "CHERRY_SIGN"
        }) {
            mapping.put(sign, Fuel.of(200));
        }
        for (String tool : new String[]{
                "WOODEN_PICKAXE",
                "WOODEN_SHOVEL",
                "WOODEN_HOE",
                "WOODEN_AXE",
                "WOODEN_SWORD"
        }) {
            mapping.put(tool, Fuel.of(200));
        }
        for (String slab : new String[]{
                "OAK_SLAB",
                "BIRCH_SLAB",
                "SPRUCE_SLAB",
                "JUNGLE_SLAB",
                "ACACIA_SLAB",
                "DARK_OAK_SLAB",
                "MANGROVE_SLAB",
                "BAMBOO_SLAB",
                "CHERRY_SLAB"
        }) {
            mapping.put(slab, Fuel.of(150));
        }
        for (String button : new String[]{
                "OAK_BUTTON",
                "BIRCH_BUTTON",
                "SPRUCE_BUTTON",
                "JUNGLE_BUTTON",
                "ACACIA_BUTTON",
                "DARK_OAK_BUTTON",
                "MANGROVE_BUTTON",
                "BAMBOO_BUTTON",
                "CHERRY_BUTTON"
        }) {
            mapping.put(button, Fuel.of(100));
        }
        for (String saplingMaterial : new String[]{
                "OAK_SAPLING",
                "BIRCH_SAPLING",
                "SPRUCE_SAPLING",
                "JUNGLE_SAPLING",
                "ACACIA_SAPLING",
                "DARK_OAK_SAPLING",
                "MANGROVE_PROPAGULE",
                "BAMBOO_SAPLING",
                "CHERRY_SAPLING"
        }) {
            mapping.put(saplingMaterial, Fuel.of(100));
        }
        for (String woolMaterial : new String[]{
                "WHITE_WOOL",
                "ORANGE_WOOL",
                "MAGENTA_WOOL",
                "LIGHT_BLUE_WOOL",
                "YELLOW_WOOL",
                "LIME_WOOL",
                "PINK_WOOL",
                "GRAY_WOOL",
                "LIGHT_GRAY_WOOL",
                "CYAN_WOOL",
                "PURPLE_WOOL",
                "BLUE_WOOL",
                "BROWN_WOOL",
                "GREEN_WOOL",
                "RED_WOOL",
                "BLACK_WOOL",
        }) {
            mapping.put(woolMaterial, Fuel.of(100));
        }
        for (String carpetMaterial : new String[]{
                "WHITE_CARPET",
                "ORANGE_CARPET",
                "MAGENTA_CARPET",
                "LIGHT_BLUE_CARPET",
                "YELLOW_CARPET",
                "LIME_CARPET",
                "PINK_CARPET",
                "GRAY_CARPET",
                "LIGHT_GRAY_CARPET",
                "CYAN_CARPET",
                "PURPLE_CARPET",
                "BLUE_CARPET",
                "BROWN_CARPET",
                "GREEN_CARPET",
                "RED_CARPET",
                "BLACK_CARPET",
        }) {
            mapping.put(carpetMaterial, Fuel.of(67));
        }
        mapping.put("BAMBOO", Fuel.of(50));
    }

    public static FuelAPI getInstance() {
        if (instance == null) {
            FuelAPI.instance = new FuelAPI();
        }
        return instance;
    }


    private final Map<String, Fuel> mapping = new HashMap<>();

    /**
     * Checks if the material is a fuel, whether
     * it belongs to a custom item or not.
     * NATIVE AND TESTED FOR 1.19.4!
     *
     * @param material The material to check
     * @return A Result containing the Fuel if it is a fuel, an invalid Result otherwise.
     */
    public Result<Fuel> isFuel(Material material) {
        Fuel fuel = mapping.get(material.toString());
        if (fuel == null)
            return Result.invalidBecauseNull();
        return Result.ofNullable(fuel);
    }
}