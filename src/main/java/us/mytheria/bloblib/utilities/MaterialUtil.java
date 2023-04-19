package us.mytheria.bloblib.utilities;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.util.HashMap;

public class MaterialUtil {
    private static boolean isValidMaterial(String material) {
        try {
            Field field = Material.class.getField(material);
            return field != null;
        } catch (NoSuchFieldError | NoSuchFieldException e) {
            return false;
        }
    }

    private static boolean isValidEntityType(String entityType) {
        try {
            Field field = EntityType.class.getField(entityType);
            return field != null;
        } catch (NoSuchFieldError | NoSuchFieldException e) {
            return false;
        }
    }

    private static void safeAddMaterial(HashMap<Material, Material> map, String key, String value) {
        if (isValidMaterial(key) && isValidMaterial(value)) {
            map.put(Material.valueOf(key), Material.valueOf(value));
        }
    }

    private static void safeAddEntity(HashMap<EntityType, Material> map, String key, String value) {
        if (isValidEntityType(key) && isValidMaterial(value)) {
            map.put(EntityType.valueOf(key), Material.valueOf(value));
        }
    }

    public static void fillNonItem(HashMap<Material, Material> map) {
        safeAddMaterial(map, "WATER", "WATER_BUCKET");
        safeAddMaterial(map, "LAVA", "LAVA_BUCKET");
        safeAddMaterial(map, "TALL_SEAGRASS", "SEAGRASS");
        safeAddMaterial(map, "PISTON_HEAD", "PISTON");
        safeAddMaterial(map, "MOVING_PISTON", "PISTON");
        safeAddMaterial(map, "WALL_TORCH", "TORCH");
        safeAddMaterial(map, "FIRE", "CAMPFIRE");
        safeAddMaterial(map, "SOUL_FIRE", "SOUL_CAMPFIRE");
        safeAddMaterial(map, "REDSTONE_WIRE", "REDSTONE");
        safeAddMaterial(map, "OAK_WALL_SIGN", "OAK_SIGN");
        safeAddMaterial(map, "SPRUCE_WALL_SIGN", "SPRUCE_SIGN");
        safeAddMaterial(map, "BIRCH_WALL_SIGN", "BIRCH_SIGN");
        safeAddMaterial(map, "ACACIA_WALL_SIGN", "ACACIA_SIGN");
        safeAddMaterial(map, "JUNGLE_WALL_SIGN", "JUNGLE_SIGN");
        safeAddMaterial(map, "DARK_OAK_WALL_SIGN", "DARK_OAK_SIGN");
        safeAddMaterial(map, "MANGROVE_WALL_SIGN", "MANGROVE_SIGN");
        safeAddMaterial(map, "REDSTONE_WALL_TORCH", "REDSTONE_TORCH");
        safeAddMaterial(map, "SOUL_WALL_TORCH", "SOUL_TORCH");
        safeAddMaterial(map, "NETHER_PORTAL", "OBSIDIAN");
        safeAddMaterial(map, "ATTACHED_PUMPKIN_STEM", "PUMPKIN");
        safeAddMaterial(map, "ATTACHED_MELON_STEM", "MELON");
        safeAddMaterial(map, "PUMPKIN_STEM", "PUMPKIN");
        safeAddMaterial(map, "MELON_STEM", "MELON");
        safeAddMaterial(map, "WATER_CAULDRON", "CAULDRON");
        safeAddMaterial(map, "LAVA_CAULDRON", "CAULDRON");
        safeAddMaterial(map, "POWDER_SNOW_CAULDRON", "CAULDRON");
        safeAddMaterial(map, "END_PORTAL", "END_PORTAL_FRAME");
        safeAddMaterial(map, "COCOA", "COCOA_BEANS");
        safeAddMaterial(map, "TRIPWIRE", "STRING");
        safeAddMaterial(map, "POTTED_OAK_SAPLING", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_SPRUCE_SAPLING", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_BIRCH_SAPLING", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_JUNGLE_SAPLING", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_ACACIA_SAPLING", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_DARK_OAK_SAPLING", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_MANGROVE_PROPAGULE", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_FERN", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_DANDELION", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_POPPY", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_BLUE_ORCHID", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_ALLIUM", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_AZURE_BLUET", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_RED_TULIP", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_ORANGE_TULIP", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_WHITE_TULIP", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_PINK_TULIP", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_OXEYE_DAISY", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_CORNFLOWER", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_LILY_OF_THE_VALLEY", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_WITHER_ROSE", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_RED_MUSHROOM", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_BROWN_MUSHROOM", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_DEAD_BUSH", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_CACTUS", "FLOWER_POT");
        safeAddMaterial(map, "CARROTS", "CARROT");
        safeAddMaterial(map, "POTATOES", "POTATO");
        safeAddMaterial(map, "SKELETON_WALL_SKULL", "SKELETON_SKULL");
        safeAddMaterial(map, "WITHER_SKELETON_WALL_SKULL", "WITHER_SKELETON_SKULL");
        safeAddMaterial(map, "ZOMBIE_WALL_HEAD", "ZOMBIE_HEAD");
        safeAddMaterial(map, "PLAYER_WALL_HEAD", "PLAYER_HEAD");
        safeAddMaterial(map, "CREEPER_WALL_HEAD", "CREEPER_HEAD");
        safeAddMaterial(map, "DRAGON_WALL_HEAD", "DRAGON_HEAD");
        safeAddMaterial(map, "WHITE_WALL_BANNER", "WHITE_BANNER");
        safeAddMaterial(map, "ORANGE_WALL_BANNER", "ORANGE_BANNER");
        safeAddMaterial(map, "MAGENTA_WALL_BANNER", "MAGENTA_BANNER");
        safeAddMaterial(map, "LIGHT_BLUE_WALL_BANNER", "LIGHT_BLUE_BANNER");
        safeAddMaterial(map, "YELLOW_WALL_BANNER", "YELLOW_BANNER");
        safeAddMaterial(map, "LIME_WALL_BANNER", "LIME_BANNER");
        safeAddMaterial(map, "PINK_WALL_BANNER", "PINK_BANNER");
        safeAddMaterial(map, "GRAY_WALL_BANNER", "GRAY_BANNER");
        safeAddMaterial(map, "LIGHT_GRAY_WALL_BANNER", "LIGHT_GRAY_BANNER");
        safeAddMaterial(map, "CYAN_WALL_BANNER", "CYAN_BANNER");
        safeAddMaterial(map, "PURPLE_WALL_BANNER", "PURPLE_BANNER");
        safeAddMaterial(map, "BLUE_WALL_BANNER", "BLUE_BANNER");
        safeAddMaterial(map, "BROWN_WALL_BANNER", "BROWN_BANNER");
        safeAddMaterial(map, "GREEN_WALL_BANNER", "GREEN_BANNER");
        safeAddMaterial(map, "RED_WALL_BANNER", "RED_BANNER");
        safeAddMaterial(map, "BLACK_WALL_BANNER", "BLACK_BANNER");
        safeAddMaterial(map, "BEETROOTS", "BEETROOT");
        safeAddMaterial(map, "END_GATEWAY", "END_PORTAL_FRAME");
        safeAddMaterial(map, "FROSTED_ICE", "ICE");
        safeAddMaterial(map, "KELP_PLANT", "KELP");
        safeAddMaterial(map, "DEAD_TUBE_CORAL_WALL_FAN", "DEAD_TUBE_CORAL_FAN");
        safeAddMaterial(map, "DEAD_BRAIN_CORAL_WALL_FAN", "DEAD_BRAIN_CORAL_FAN");
        safeAddMaterial(map, "DEAD_BUBBLE_CORAL_WALL_FAN", "DEAD_BUBBLE_CORAL_FAN");
        safeAddMaterial(map, "DEAD_FIRE_CORAL_WALL_FAN", "DEAD_FIRE_CORAL_FAN");
        safeAddMaterial(map, "DEAD_HORN_CORAL_WALL_FAN", "DEAD_HORN_CORAL_FAN");
        safeAddMaterial(map, "TUBE_CORAL_WALL_FAN", "TUBE_CORAL_FAN");
        safeAddMaterial(map, "BRAIN_CORAL_WALL_FAN", "BRAIN_CORAL_FAN");
        safeAddMaterial(map, "BUBBLE_CORAL_WALL_FAN", "BUBBLE_CORAL_FAN");
        safeAddMaterial(map, "FIRE_CORAL_WALL_FAN", "FIRE_CORAL_FAN");
        safeAddMaterial(map, "HORN_CORAL_WALL_FAN", "HORN_CORAL_FAN");
        safeAddMaterial(map, "BAMBOO_SAPLING", "BAMBOO");
        safeAddMaterial(map, "POTTED_BAMBOO", "FLOWER_POT");
        safeAddMaterial(map, "VOID_AIR", "GLASS");
        safeAddMaterial(map, "CAVE_AIR", "GLASS");
        safeAddMaterial(map, "BUBBLE_COLUMN", "WATER_BUCKET");
        safeAddMaterial(map, "SWEET_BERRY_BUSH", "SWEET_BERRIES");
        safeAddMaterial(map, "WEEPING_VINES_PLANT", "WEEPING_VINES");
        safeAddMaterial(map, "TWISTING_VINES_PLANT", "TWISTING_VINES");
        safeAddMaterial(map, "CRIMSON_WALL_SIGN", "CRIMSON_SIGN");
        safeAddMaterial(map, "WARPED_WALL_SIGN", "WARPED_SIGN");
        safeAddMaterial(map, "POTTED_CRIMSON_FUNGUS", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_WARPED_FUNGUS", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_CRIMSON_ROOTS", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_WARPED_ROOTS", "FLOWER_POT");
        safeAddMaterial(map, "CANDLE_CAKE", "CANDLE");
        safeAddMaterial(map, "WHITE_CANDLE_CAKE", "WHITE_CANDLE");
        safeAddMaterial(map, "ORANGE_CANDLE_CAKE", "ORANGE_CANDLE");
        safeAddMaterial(map, "MAGENTA_CANDLE_CAKE", "MAGENTA_CANDLE");
        safeAddMaterial(map, "LIGHT_BLUE_CANDLE_CAKE", "LIGHT_BLUE_CANDLE");
        safeAddMaterial(map, "YELLOW_CANDLE_CAKE", "YELLOW_CANDLE");
        safeAddMaterial(map, "LIME_CANDLE_CAKE", "LIME_CANDLE");
        safeAddMaterial(map, "PINK_CANDLE_CAKE", "PINK_CANDLE");
        safeAddMaterial(map, "GRAY_CANDLE_CAKE", "GRAY_CANDLE");
        safeAddMaterial(map, "LIGHT_GRAY_CANDLE_CAKE", "LIGHT_GRAY_CANDLE");
        safeAddMaterial(map, "CYAN_CANDLE_CAKE", "CYAN_CANDLE");
        safeAddMaterial(map, "PURPLE_CANDLE_CAKE", "PURPLE_CANDLE");
        safeAddMaterial(map, "BLUE_CANDLE_CAKE", "BLUE_CANDLE");
        safeAddMaterial(map, "BROWN_CANDLE_CAKE", "BROWN_CANDLE");
        safeAddMaterial(map, "GREEN_CANDLE_CAKE", "GREEN_CANDLE");
        safeAddMaterial(map, "RED_CANDLE_CAKE", "RED_CANDLE");
        safeAddMaterial(map, "BLACK_CANDLE_CAKE", "BLACK_CANDLE");
        safeAddMaterial(map, "POWDER_SNOW", "SNOWBALL");
        safeAddMaterial(map, "CAVE_VINES", "GLOW_BERRIES");
        safeAddMaterial(map, "CAVE_VINES_PLANT", "GLOW_BERRIES");
        safeAddMaterial(map, "BIG_DRIPLEAF_STEM", "BIG_DRIPLEAF");
        safeAddMaterial(map, "POTTED_AZALEA_BUSH", "FLOWER_POT");
        safeAddMaterial(map, "POTTED_FLOWERING_AZALEA_BUSH", "FLOWER_POT");
    }

    public static void fillEntityTypeMaterial(HashMap<EntityType, Material> map) {
        safeAddEntity(map, "EXPERIENCE_ORB", "EXPERIENCE_BOTTLE");
        safeAddEntity(map, "AREA_EFFECT_CLOUD", "POTION");
        safeAddEntity(map, "ELDER_GUARDIAN", "ELDER_GUARDIAN_SPAWN_EGG");
        safeAddEntity(map, "WITHER_SKELETON", "WITHER_SKELETON_SPAWN_EGG");
        safeAddEntity(map, "STRAY", "STRAY_SPAWN_EGG");
        safeAddEntity(map, "EGG", "EGG");
        safeAddEntity(map, "LEASH_HITCH", "LEAD");
        safeAddEntity(map, "PAINTING", "PAINTING");
        safeAddEntity(map, "ARROW", "ARROW");
        safeAddEntity(map, "SNOWBALL", "SNOWBALL");
        safeAddEntity(map, "FIREBALL", "GHAST_SPAWN_EGG");
        safeAddEntity(map, "SMALL_FIREBALL", "FIRE_CHARGE");
        safeAddEntity(map, "ENDER_PEARL", "ENDER_PEARL");
        safeAddEntity(map, "ENDER_SIGNAL", "ENDER_EYE");
        safeAddEntity(map, "THROWN_EXP_BOTTLE", "EXPERIENCE_BOTTLE");
        safeAddEntity(map, "ITEM_FRAME", "ITEM_FRAME");
        safeAddEntity(map, "WITHER_SKULL", "WITHER_SKELETON_SKULL");
        safeAddEntity(map, "PRIMED_TNT", "TNT");
        safeAddEntity(map, "HUSK", "HUSK_SPAWN_EGG");
        safeAddEntity(map, "SPECTRAL_ARROW", "SPECTRAL_ARROW");
        safeAddEntity(map, "SHULKER_BULLET", "SHULKER_SHELL");
        safeAddEntity(map, "DRAGON_FIREBALL", "DRAGON_BREATH");
        safeAddEntity(map, "ZOMBIE_VILLAGER", "ZOMBIE_VILLAGER_SPAWN_EGG");
        safeAddEntity(map, "SKELETON_HORSE", "SKELETON_HORSE_SPAWN_EGG");
        safeAddEntity(map, "ZOMBIE_HORSE", "ZOMBIE_HORSE_SPAWN_EGG");
        safeAddEntity(map, "ARMOR_STAND", "ARMOR_STAND");
        safeAddEntity(map, "DONKEY", "DONKEY_SPAWN_EGG");
        safeAddEntity(map, "MULE", "MULE_SPAWN_EGG");
        safeAddEntity(map, "EVOKER_FANGS", "EVOKER_SPAWN_EGG");
        safeAddEntity(map, "EVOKER", "EVOKER_SPAWN_EGG");
        safeAddEntity(map, "VEX", "VEX_SPAWN_EGG");
        safeAddEntity(map, "VINDICATOR", "VINDICATOR_SPAWN_EGG");
        safeAddEntity(map, "ILLUSIONER", "COMMAND_BLOCK");
        safeAddEntity(map, "MINECART_COMMAND", "COMMAND_BLOCK_MINECART");
        safeAddEntity(map, "BOAT", "OAK_BOAT");
        safeAddEntity(map, "MINECART", "MINECART");
        safeAddEntity(map, "MINECART_CHEST", "CHEST_MINECART");
        safeAddEntity(map, "MINECART_FURNACE", "FURNACE_MINECART");
        safeAddEntity(map, "MINECART_TNT", "TNT_MINECART");
        safeAddEntity(map, "MINECART_HOPPER", "HOPPER_MINECART");
        safeAddEntity(map, "MINECART_MOB_SPAWNER", "SPAWNER");
        safeAddEntity(map, "CREEPER", "CREEPER_SPAWN_EGG");
        safeAddEntity(map, "SKELETON", "SKELETON_SPAWN_EGG");
        safeAddEntity(map, "SPIDER", "SPIDER_SPAWN_EGG");
        safeAddEntity(map, "GIANT", "ZOMBIE_SPAWN_EGG");
        safeAddEntity(map, "ZOMBIE", "ZOMBIE_SPAWN_EGG");
        safeAddEntity(map, "SLIME", "SLIME_SPAWN_EGG");
        safeAddEntity(map, "GHAST", "GHAST_SPAWN_EGG");
        safeAddEntity(map, "ZOMBIFIED_PIGLIN", "ZOMBIFIED_PIGLIN_SPAWN_EGG");
        safeAddEntity(map, "ENDERMAN", "ENDERMAN_SPAWN_EGG");
        safeAddEntity(map, "CAVE_SPIDER", "CAVE_SPIDER_SPAWN_EGG");
        safeAddEntity(map, "SILVERFISH", "SILVERFISH_SPAWN_EGG");
        safeAddEntity(map, "BLAZE", "BLAZE_SPAWN_EGG");
        safeAddEntity(map, "MAGMA_CUBE", "MAGMA_CUBE_SPAWN_EGG");
        safeAddEntity(map, "ENDER_DRAGON", "COMMAND_BLOCK");
        safeAddEntity(map, "WITHER", "COMMAND_BLOCK");
        safeAddEntity(map, "BAT", "BAT_SPAWN_EGG");
        safeAddEntity(map, "WITCH", "WITCH_SPAWN_EGG");
        safeAddEntity(map, "ENDERMITE", "ENDERMITE_SPAWN_EGG");
        safeAddEntity(map, "GUARDIAN", "GUARDIAN_SPAWN_EGG");
        safeAddEntity(map, "SHULKER", "SHULKER_SPAWN_EGG");
        safeAddEntity(map, "PIG", "PIG_SPAWN_EGG");
        safeAddEntity(map, "SHEEP", "SHEEP_SPAWN_EGG");
        safeAddEntity(map, "COW", "COW_SPAWN_EGG");
        safeAddEntity(map, "CHICKEN", "CHICKEN_SPAWN_EGG");
        safeAddEntity(map, "SQUID", "SQUID_SPAWN_EGG");
        safeAddEntity(map, "WOLF", "WOLF_SPAWN_EGG");
        safeAddEntity(map, "MUSHROOM_COW", "MOOSHROOM_SPAWN_EGG");
        safeAddEntity(map, "SNOWMAN", "CARVED_PUMPKIN");
        safeAddEntity(map, "OCELOT", "OCELOT_SPAWN_EGG");
        safeAddEntity(map, "IRON_GOLEM", "CARVED_PUMPKIN");
        safeAddEntity(map, "HORSE", "HORSE_SPAWN_EGG");
        safeAddEntity(map, "RABBIT", "RABBIT_SPAWN_EGG");
        safeAddEntity(map, "POLAR_BEAR", "POLAR_BEAR_SPAWN_EGG");
        safeAddEntity(map, "LLAMA", "LLAMA_SPAWN_EGG");
        safeAddEntity(map, "LLAMA_SPIT", "LLAMA_SPAWN_EGG");
        safeAddEntity(map, "PARROT", "PARROT_SPAWN_EGG");
        safeAddEntity(map, "VILLAGER", "VILLAGER_SPAWN_EGG");
        safeAddEntity(map, "ENDER_CRYSTAL", "END_CRYSTAL");
        safeAddEntity(map, "TURTLE", "TURTLE_SPAWN_EGG");
        safeAddEntity(map, "PHANTOM", "PHANTOM_SPAWN_EGG");
        safeAddEntity(map, "TRIDENT", "TRIDENT");
        safeAddEntity(map, "COD", "COD_SPAWN_EGG");
        safeAddEntity(map, "SALMON", "SALMON_SPAWN_EGG");
        safeAddEntity(map, "PUFFERFISH", "PUFFERFISH_SPAWN_EGG");
        safeAddEntity(map, "TROPICAL_FISH", "TROPICAL_FISH_SPAWN_EGG");
        safeAddEntity(map, "DROWNED", "DROWNED_SPAWN_EGG");
        safeAddEntity(map, "DOLPHIN", "DOLPHIN_SPAWN_EGG");
        safeAddEntity(map, "CAT", "CAT_SPAWN_EGG");
        safeAddEntity(map, "PANDA", "PANDA_SPAWN_EGG");
        safeAddEntity(map, "PILLAGER", "PILLAGER_SPAWN_EGG");
        safeAddEntity(map, "RAVAGER", "RAVAGER_SPAWN_EGG");
        safeAddEntity(map, "TRADER_LLAMA", "TRADER_LLAMA_SPAWN_EGG");
        safeAddEntity(map, "WANDERING_TRADER", "WANDERING_TRADER_SPAWN_EGG");
        safeAddEntity(map, "FOX", "FOX_SPAWN_EGG");
        safeAddEntity(map, "BEE", "BEE_SPAWN_EGG");
        safeAddEntity(map, "HOGLIN", "HOGLIN_SPAWN_EGG");
        safeAddEntity(map, "PIGLIN", "PIGLIN_SPAWN_EGG");
        safeAddEntity(map, "STRIDER", "STRIDER_SPAWN_EGG");
        safeAddEntity(map, "ZOGLIN", "ZOGLIN_SPAWN_EGG");
        safeAddEntity(map, "PIGLIN_BRUTE", "PIGLIN_BRUTE_SPAWN_EGG");
        safeAddEntity(map, "AXOLOTL", "AXOLOTL_SPAWN_EGG");
        safeAddEntity(map, "GLOW_ITEM_FRAME", "GLOW_ITEM_FRAME");
        safeAddEntity(map, "GLOW_SQUID", "GLOW_SQUID_SPAWN_EGG");
        safeAddEntity(map, "GOAT", "GOAT_SPAWN_EGG");
        safeAddEntity(map, "MARKER", "MAP");
        safeAddEntity(map, "ALLAY", "ALLAY_SPAWN_EGG");
        safeAddEntity(map, "CHEST_BOAT", "OAK_CHEST_BOAT");
        safeAddEntity(map, "FROG", "FROG_SPAWN_EGG");
        safeAddEntity(map, "TADPOLE", "TADPOLE_SPAWN_EGG");
        safeAddEntity(map, "WARDEN", "WARDEN_SPAWN_EGG");
    }
}
