package us.mytheria.bloblib.utilities;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class MaterialUtil {
    private static final Map<Material, Material> nonItem = new HashMap<>();

    private static final Map<EntityType, Material> entityToMaterial = new HashMap<>();

    static {
        safeAddMaterial(nonItem, "WATER", "WATER_BUCKET");
        safeAddMaterial(nonItem, "LAVA", "LAVA_BUCKET");
        safeAddMaterial(nonItem, "TALL_SEAGRASS", "SEAGRASS");
        safeAddMaterial(nonItem, "PISTON_HEAD", "PISTON");
        safeAddMaterial(nonItem, "MOVING_PISTON", "PISTON");
        safeAddMaterial(nonItem, "WALL_TORCH", "TORCH");
        safeAddMaterial(nonItem, "FIRE", "CAMPFIRE");
        safeAddMaterial(nonItem, "SOUL_FIRE", "SOUL_CAMPFIRE");
        safeAddMaterial(nonItem, "REDSTONE_WIRE", "REDSTONE");
        safeAddMaterial(nonItem, "OAK_WALL_SIGN", "OAK_SIGN");
        safeAddMaterial(nonItem, "SPRUCE_WALL_SIGN", "SPRUCE_SIGN");
        safeAddMaterial(nonItem, "BIRCH_WALL_SIGN", "BIRCH_SIGN");
        safeAddMaterial(nonItem, "ACACIA_WALL_SIGN", "ACACIA_SIGN");
        safeAddMaterial(nonItem, "JUNGLE_WALL_SIGN", "JUNGLE_SIGN");
        safeAddMaterial(nonItem, "DARK_OAK_WALL_SIGN", "DARK_OAK_SIGN");
        safeAddMaterial(nonItem, "MANGROVE_WALL_SIGN", "MANGROVE_SIGN");
        safeAddMaterial(nonItem, "REDSTONE_WALL_TORCH", "REDSTONE_TORCH");
        safeAddMaterial(nonItem, "SOUL_WALL_TORCH", "SOUL_TORCH");
        safeAddMaterial(nonItem, "NETHER_PORTAL", "OBSIDIAN");
        safeAddMaterial(nonItem, "ATTACHED_PUMPKIN_STEM", "PUMPKIN");
        safeAddMaterial(nonItem, "ATTACHED_MELON_STEM", "MELON");
        safeAddMaterial(nonItem, "PUMPKIN_STEM", "PUMPKIN");
        safeAddMaterial(nonItem, "MELON_STEM", "MELON");
        safeAddMaterial(nonItem, "WATER_CAULDRON", "CAULDRON");
        safeAddMaterial(nonItem, "LAVA_CAULDRON", "CAULDRON");
        safeAddMaterial(nonItem, "POWDER_SNOW_CAULDRON", "CAULDRON");
        safeAddMaterial(nonItem, "END_PORTAL", "END_PORTAL_FRAME");
        safeAddMaterial(nonItem, "COCOA", "COCOA_BEANS");
        safeAddMaterial(nonItem, "TRIPWIRE", "STRING");
        safeAddMaterial(nonItem, "POTTED_OAK_SAPLING", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_SPRUCE_SAPLING", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_BIRCH_SAPLING", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_JUNGLE_SAPLING", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_ACACIA_SAPLING", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_DARK_OAK_SAPLING", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_MANGROVE_PROPAGULE", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_FERN", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_DANDELION", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_POPPY", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_BLUE_ORCHID", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_ALLIUM", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_AZURE_BLUET", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_RED_TULIP", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_ORANGE_TULIP", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_WHITE_TULIP", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_PINK_TULIP", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_OXEYE_DAISY", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_CORNFLOWER", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_LILY_OF_THE_VALLEY", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_WITHER_ROSE", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_RED_MUSHROOM", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_BROWN_MUSHROOM", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_DEAD_BUSH", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_CACTUS", "FLOWER_POT");
        safeAddMaterial(nonItem, "CARROTS", "CARROT");
        safeAddMaterial(nonItem, "POTATOES", "POTATO");
        safeAddMaterial(nonItem, "SKELETON_WALL_SKULL", "SKELETON_SKULL");
        safeAddMaterial(nonItem, "WITHER_SKELETON_WALL_SKULL", "WITHER_SKELETON_SKULL");
        safeAddMaterial(nonItem, "ZOMBIE_WALL_HEAD", "ZOMBIE_HEAD");
        safeAddMaterial(nonItem, "PLAYER_WALL_HEAD", "PLAYER_HEAD");
        safeAddMaterial(nonItem, "CREEPER_WALL_HEAD", "CREEPER_HEAD");
        safeAddMaterial(nonItem, "DRAGON_WALL_HEAD", "DRAGON_HEAD");
        safeAddMaterial(nonItem, "WHITE_WALL_BANNER", "WHITE_BANNER");
        safeAddMaterial(nonItem, "ORANGE_WALL_BANNER", "ORANGE_BANNER");
        safeAddMaterial(nonItem, "MAGENTA_WALL_BANNER", "MAGENTA_BANNER");
        safeAddMaterial(nonItem, "LIGHT_BLUE_WALL_BANNER", "LIGHT_BLUE_BANNER");
        safeAddMaterial(nonItem, "YELLOW_WALL_BANNER", "YELLOW_BANNER");
        safeAddMaterial(nonItem, "LIME_WALL_BANNER", "LIME_BANNER");
        safeAddMaterial(nonItem, "PINK_WALL_BANNER", "PINK_BANNER");
        safeAddMaterial(nonItem, "GRAY_WALL_BANNER", "GRAY_BANNER");
        safeAddMaterial(nonItem, "LIGHT_GRAY_WALL_BANNER", "LIGHT_GRAY_BANNER");
        safeAddMaterial(nonItem, "CYAN_WALL_BANNER", "CYAN_BANNER");
        safeAddMaterial(nonItem, "PURPLE_WALL_BANNER", "PURPLE_BANNER");
        safeAddMaterial(nonItem, "BLUE_WALL_BANNER", "BLUE_BANNER");
        safeAddMaterial(nonItem, "BROWN_WALL_BANNER", "BROWN_BANNER");
        safeAddMaterial(nonItem, "GREEN_WALL_BANNER", "GREEN_BANNER");
        safeAddMaterial(nonItem, "RED_WALL_BANNER", "RED_BANNER");
        safeAddMaterial(nonItem, "BLACK_WALL_BANNER", "BLACK_BANNER");
        safeAddMaterial(nonItem, "BEETROOTS", "BEETROOT");
        safeAddMaterial(nonItem, "END_GATEWAY", "END_PORTAL_FRAME");
        safeAddMaterial(nonItem, "FROSTED_ICE", "ICE");
        safeAddMaterial(nonItem, "KELP_PLANT", "KELP");
        safeAddMaterial(nonItem, "DEAD_TUBE_CORAL_WALL_FAN", "DEAD_TUBE_CORAL_FAN");
        safeAddMaterial(nonItem, "DEAD_BRAIN_CORAL_WALL_FAN", "DEAD_BRAIN_CORAL_FAN");
        safeAddMaterial(nonItem, "DEAD_BUBBLE_CORAL_WALL_FAN", "DEAD_BUBBLE_CORAL_FAN");
        safeAddMaterial(nonItem, "DEAD_FIRE_CORAL_WALL_FAN", "DEAD_FIRE_CORAL_FAN");
        safeAddMaterial(nonItem, "DEAD_HORN_CORAL_WALL_FAN", "DEAD_HORN_CORAL_FAN");
        safeAddMaterial(nonItem, "TUBE_CORAL_WALL_FAN", "TUBE_CORAL_FAN");
        safeAddMaterial(nonItem, "BRAIN_CORAL_WALL_FAN", "BRAIN_CORAL_FAN");
        safeAddMaterial(nonItem, "BUBBLE_CORAL_WALL_FAN", "BUBBLE_CORAL_FAN");
        safeAddMaterial(nonItem, "FIRE_CORAL_WALL_FAN", "FIRE_CORAL_FAN");
        safeAddMaterial(nonItem, "HORN_CORAL_WALL_FAN", "HORN_CORAL_FAN");
        safeAddMaterial(nonItem, "BAMBOO_SAPLING", "BAMBOO");
        safeAddMaterial(nonItem, "POTTED_BAMBOO", "FLOWER_POT");
        safeAddMaterial(nonItem, "VOID_AIR", "GLASS");
        safeAddMaterial(nonItem, "CAVE_AIR", "GLASS");
        safeAddMaterial(nonItem, "BUBBLE_COLUMN", "WATER_BUCKET");
        safeAddMaterial(nonItem, "SWEET_BERRY_BUSH", "SWEET_BERRIES");
        safeAddMaterial(nonItem, "WEEPING_VINES_PLANT", "WEEPING_VINES");
        safeAddMaterial(nonItem, "TWISTING_VINES_PLANT", "TWISTING_VINES");
        safeAddMaterial(nonItem, "CRIMSON_WALL_SIGN", "CRIMSON_SIGN");
        safeAddMaterial(nonItem, "WARPED_WALL_SIGN", "WARPED_SIGN");
        safeAddMaterial(nonItem, "POTTED_CRIMSON_FUNGUS", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_WARPED_FUNGUS", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_CRIMSON_ROOTS", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_WARPED_ROOTS", "FLOWER_POT");
        safeAddMaterial(nonItem, "CANDLE_CAKE", "CANDLE");
        safeAddMaterial(nonItem, "WHITE_CANDLE_CAKE", "WHITE_CANDLE");
        safeAddMaterial(nonItem, "ORANGE_CANDLE_CAKE", "ORANGE_CANDLE");
        safeAddMaterial(nonItem, "MAGENTA_CANDLE_CAKE", "MAGENTA_CANDLE");
        safeAddMaterial(nonItem, "LIGHT_BLUE_CANDLE_CAKE", "LIGHT_BLUE_CANDLE");
        safeAddMaterial(nonItem, "YELLOW_CANDLE_CAKE", "YELLOW_CANDLE");
        safeAddMaterial(nonItem, "LIME_CANDLE_CAKE", "LIME_CANDLE");
        safeAddMaterial(nonItem, "PINK_CANDLE_CAKE", "PINK_CANDLE");
        safeAddMaterial(nonItem, "GRAY_CANDLE_CAKE", "GRAY_CANDLE");
        safeAddMaterial(nonItem, "LIGHT_GRAY_CANDLE_CAKE", "LIGHT_GRAY_CANDLE");
        safeAddMaterial(nonItem, "CYAN_CANDLE_CAKE", "CYAN_CANDLE");
        safeAddMaterial(nonItem, "PURPLE_CANDLE_CAKE", "PURPLE_CANDLE");
        safeAddMaterial(nonItem, "BLUE_CANDLE_CAKE", "BLUE_CANDLE");
        safeAddMaterial(nonItem, "BROWN_CANDLE_CAKE", "BROWN_CANDLE");
        safeAddMaterial(nonItem, "GREEN_CANDLE_CAKE", "GREEN_CANDLE");
        safeAddMaterial(nonItem, "RED_CANDLE_CAKE", "RED_CANDLE");
        safeAddMaterial(nonItem, "BLACK_CANDLE_CAKE", "BLACK_CANDLE");
        safeAddMaterial(nonItem, "POWDER_SNOW", "SNOWBALL");
        safeAddMaterial(nonItem, "CAVE_VINES", "GLOW_BERRIES");
        safeAddMaterial(nonItem, "CAVE_VINES_PLANT", "GLOW_BERRIES");
        safeAddMaterial(nonItem, "BIG_DRIPLEAF_STEM", "BIG_DRIPLEAF");
        safeAddMaterial(nonItem, "POTTED_AZALEA_BUSH", "FLOWER_POT");
        safeAddMaterial(nonItem, "POTTED_FLOWERING_AZALEA_BUSH", "FLOWER_POT");

        safeAddEntity(entityToMaterial, "EXPERIENCE_ORB", "EXPERIENCE_BOTTLE");
        safeAddEntity(entityToMaterial, "AREA_EFFECT_CLOUD", "POTION");
        safeAddEntity(entityToMaterial, "ELDER_GUARDIAN", "ELDER_GUARDIAN_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "WITHER_SKELETON", "WITHER_SKELETON_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "STRAY", "STRAY_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "EGG", "EGG");
        safeAddEntity(entityToMaterial, "LEASH_HITCH", "LEAD");
        safeAddEntity(entityToMaterial, "PAINTING", "PAINTING");
        safeAddEntity(entityToMaterial, "ARROW", "ARROW");
        safeAddEntity(entityToMaterial, "SNOWBALL", "SNOWBALL");
        safeAddEntity(entityToMaterial, "FIREBALL", "GHAST_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "SMALL_FIREBALL", "FIRE_CHARGE");
        safeAddEntity(entityToMaterial, "ENDER_PEARL", "ENDER_PEARL");
        safeAddEntity(entityToMaterial, "ENDER_SIGNAL", "ENDER_EYE");
        safeAddEntity(entityToMaterial, "THROWN_EXP_BOTTLE", "EXPERIENCE_BOTTLE");
        safeAddEntity(entityToMaterial, "ITEM_FRAME", "ITEM_FRAME");
        safeAddEntity(entityToMaterial, "WITHER_SKULL", "WITHER_SKELETON_SKULL");
        safeAddEntity(entityToMaterial, "PRIMED_TNT", "TNT");
        safeAddEntity(entityToMaterial, "HUSK", "HUSK_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "SPECTRAL_ARROW", "SPECTRAL_ARROW");
        safeAddEntity(entityToMaterial, "SHULKER_BULLET", "SHULKER_SHELL");
        safeAddEntity(entityToMaterial, "DRAGON_FIREBALL", "DRAGON_BREATH");
        safeAddEntity(entityToMaterial, "ZOMBIE_VILLAGER", "ZOMBIE_VILLAGER_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "SKELETON_HORSE", "SKELETON_HORSE_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "ZOMBIE_HORSE", "ZOMBIE_HORSE_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "ARMOR_STAND", "ARMOR_STAND");
        safeAddEntity(entityToMaterial, "DONKEY", "DONKEY_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "MULE", "MULE_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "EVOKER_FANGS", "EVOKER_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "EVOKER", "EVOKER_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "VEX", "VEX_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "VINDICATOR", "VINDICATOR_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "ILLUSIONER", "COMMAND_BLOCK");
        safeAddEntity(entityToMaterial, "MINECART_COMMAND", "COMMAND_BLOCK_MINECART");
        safeAddEntity(entityToMaterial, "BOAT", "OAK_BOAT");
        safeAddEntity(entityToMaterial, "MINECART", "MINECART");
        safeAddEntity(entityToMaterial, "MINECART_CHEST", "CHEST_MINECART");
        safeAddEntity(entityToMaterial, "MINECART_FURNACE", "FURNACE_MINECART");
        safeAddEntity(entityToMaterial, "MINECART_TNT", "TNT_MINECART");
        safeAddEntity(entityToMaterial, "MINECART_HOPPER", "HOPPER_MINECART");
        safeAddEntity(entityToMaterial, "MINECART_MOB_SPAWNER", "SPAWNER");
        safeAddEntity(entityToMaterial, "CREEPER", "CREEPER_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "SKELETON", "SKELETON_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "SPIDER", "SPIDER_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "GIANT", "ZOMBIE_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "ZOMBIE", "ZOMBIE_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "SLIME", "SLIME_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "GHAST", "GHAST_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "ZOMBIFIED_PIGLIN", "ZOMBIFIED_PIGLIN_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "ENDERMAN", "ENDERMAN_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "CAVE_SPIDER", "CAVE_SPIDER_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "SILVERFISH", "SILVERFISH_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "BLAZE", "BLAZE_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "MAGMA_CUBE", "MAGMA_CUBE_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "ENDER_DRAGON", "COMMAND_BLOCK");
        safeAddEntity(entityToMaterial, "WITHER", "COMMAND_BLOCK");
        safeAddEntity(entityToMaterial, "BAT", "BAT_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "WITCH", "WITCH_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "ENDERMITE", "ENDERMITE_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "GUARDIAN", "GUARDIAN_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "SHULKER", "SHULKER_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "PIG", "PIG_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "SHEEP", "SHEEP_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "COW", "COW_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "CHICKEN", "CHICKEN_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "SQUID", "SQUID_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "WOLF", "WOLF_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "MUSHROOM_COW", "MOOSHROOM_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "SNOWMAN", "CARVED_PUMPKIN");
        safeAddEntity(entityToMaterial, "OCELOT", "OCELOT_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "IRON_GOLEM", "CARVED_PUMPKIN");
        safeAddEntity(entityToMaterial, "HORSE", "HORSE_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "RABBIT", "RABBIT_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "POLAR_BEAR", "POLAR_BEAR_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "LLAMA", "LLAMA_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "LLAMA_SPIT", "LLAMA_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "PARROT", "PARROT_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "VILLAGER", "VILLAGER_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "ENDER_CRYSTAL", "END_CRYSTAL");
        safeAddEntity(entityToMaterial, "TURTLE", "TURTLE_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "PHANTOM", "PHANTOM_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "TRIDENT", "TRIDENT");
        safeAddEntity(entityToMaterial, "COD", "COD_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "SALMON", "SALMON_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "PUFFERFISH", "PUFFERFISH_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "TROPICAL_FISH", "TROPICAL_FISH_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "DROWNED", "DROWNED_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "DOLPHIN", "DOLPHIN_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "CAT", "CAT_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "PANDA", "PANDA_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "PILLAGER", "PILLAGER_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "RAVAGER", "RAVAGER_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "TRADER_LLAMA", "TRADER_LLAMA_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "WANDERING_TRADER", "WANDERING_TRADER_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "FOX", "FOX_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "BEE", "BEE_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "HOGLIN", "HOGLIN_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "PIGLIN", "PIGLIN_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "STRIDER", "STRIDER_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "ZOGLIN", "ZOGLIN_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "PIGLIN_BRUTE", "PIGLIN_BRUTE_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "AXOLOTL", "AXOLOTL_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "GLOW_ITEM_FRAME", "GLOW_ITEM_FRAME");
        safeAddEntity(entityToMaterial, "GLOW_SQUID", "GLOW_SQUID_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "GOAT", "GOAT_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "MARKER", "MAP");
        safeAddEntity(entityToMaterial, "ALLAY", "ALLAY_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "CHEST_BOAT", "OAK_CHEST_BOAT");
        safeAddEntity(entityToMaterial, "FROG", "FROG_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "TADPOLE", "TADPOLE_SPAWN_EGG");
        safeAddEntity(entityToMaterial, "WARDEN", "WARDEN_SPAWN_EGG");
    }

    private static boolean isValidMaterial(String material) {
        return Material.getMaterial(material) != null;
    }

    private static boolean isValidEntityType(String entityType) {
        try {
            EntityType.valueOf(entityType);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static void safeAddMaterial(Map<Material, Material> map, String key, String value) {
        if (isValidMaterial(key) && isValidMaterial(value)) {
            map.put(Material.valueOf(key), Material.valueOf(value));
        }
    }

    private static void safeAddEntity(Map<EntityType, Material> map, String key, String value) {
        if (isValidEntityType(key) && isValidMaterial(value)) {
            map.put(EntityType.valueOf(key), Material.valueOf(value));
        }
    }

    /**
     * Fills a map with materials that are not items but an item material
     * was manually provided for their item representation.
     *
     * @param map the map to fill
     */
    public static void fillNonItem(Map<Material, Material> map) {
        map.putAll(nonItem);
    }

    /**
     * Fills a map with EntityTypes but an item material
     * was manually provided for their item representation.
     *
     * @param map the map to fill
     */
    public static void fillEntityTypeMaterial(Map<EntityType, Material> map) {
        map.putAll(entityToMaterial);
    }
}
