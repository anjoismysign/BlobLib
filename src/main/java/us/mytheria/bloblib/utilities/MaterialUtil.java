package us.mytheria.bloblib.utilities;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

public class MaterialUtil {

    public static void fillNonItem(HashMap<Material, Material> map) {
        map.put(Material.WATER, Material.WATER_BUCKET);
        map.put(Material.LAVA, Material.LAVA_BUCKET);
        map.put(Material.TALL_SEAGRASS, Material.SEAGRASS);
        map.put(Material.PISTON_HEAD, Material.PISTON);
        map.put(Material.MOVING_PISTON, Material.PISTON);
        map.put(Material.WALL_TORCH, Material.TORCH);
        map.put(Material.FIRE, Material.CAMPFIRE);
        map.put(Material.SOUL_FIRE, Material.SOUL_CAMPFIRE);
        map.put(Material.REDSTONE_WIRE, Material.REDSTONE);
        map.put(Material.OAK_WALL_SIGN, Material.OAK_SIGN);
        map.put(Material.SPRUCE_WALL_SIGN, Material.SPRUCE_SIGN);
        map.put(Material.BIRCH_WALL_SIGN, Material.BIRCH_SIGN);
        map.put(Material.ACACIA_WALL_SIGN, Material.ACACIA_SIGN);
        map.put(Material.JUNGLE_WALL_SIGN, Material.JUNGLE_SIGN);
        map.put(Material.DARK_OAK_WALL_SIGN, Material.DARK_OAK_SIGN);
        map.put(Material.MANGROVE_WALL_SIGN, Material.MANGROVE_SIGN);
        map.put(Material.REDSTONE_WALL_TORCH, Material.REDSTONE_TORCH);
        map.put(Material.SOUL_WALL_TORCH, Material.SOUL_TORCH);
        map.put(Material.NETHER_PORTAL, Material.OBSIDIAN);
        map.put(Material.ATTACHED_PUMPKIN_STEM, Material.PUMPKIN);
        map.put(Material.ATTACHED_MELON_STEM, Material.MELON);
        map.put(Material.PUMPKIN_STEM, Material.PUMPKIN);
        map.put(Material.MELON_STEM, Material.MELON);
        map.put(Material.WATER_CAULDRON, Material.CAULDRON);
        map.put(Material.LAVA_CAULDRON, Material.CAULDRON);
        map.put(Material.POWDER_SNOW_CAULDRON, Material.CAULDRON);
        map.put(Material.END_PORTAL, Material.END_PORTAL_FRAME);
        map.put(Material.COCOA, Material.COCOA_BEANS);
        map.put(Material.TRIPWIRE, Material.STRING);
        map.put(Material.POTTED_OAK_SAPLING, Material.FLOWER_POT);
        map.put(Material.POTTED_SPRUCE_SAPLING, Material.FLOWER_POT);
        map.put(Material.POTTED_BIRCH_SAPLING, Material.FLOWER_POT);
        map.put(Material.POTTED_JUNGLE_SAPLING, Material.FLOWER_POT);
        map.put(Material.POTTED_ACACIA_SAPLING, Material.FLOWER_POT);
        map.put(Material.POTTED_DARK_OAK_SAPLING, Material.FLOWER_POT);
        map.put(Material.POTTED_MANGROVE_PROPAGULE, Material.FLOWER_POT);
        map.put(Material.POTTED_FERN, Material.FLOWER_POT);
        map.put(Material.POTTED_DANDELION, Material.FLOWER_POT);
        map.put(Material.POTTED_POPPY, Material.FLOWER_POT);
        map.put(Material.POTTED_BLUE_ORCHID, Material.FLOWER_POT);
        map.put(Material.POTTED_ALLIUM, Material.FLOWER_POT);
        map.put(Material.POTTED_AZURE_BLUET, Material.FLOWER_POT);
        map.put(Material.POTTED_RED_TULIP, Material.FLOWER_POT);
        map.put(Material.POTTED_ORANGE_TULIP, Material.FLOWER_POT);
        map.put(Material.POTTED_WHITE_TULIP, Material.FLOWER_POT);
        map.put(Material.POTTED_PINK_TULIP, Material.FLOWER_POT);
        map.put(Material.POTTED_OXEYE_DAISY, Material.FLOWER_POT);
        map.put(Material.POTTED_CORNFLOWER, Material.FLOWER_POT);
        map.put(Material.POTTED_LILY_OF_THE_VALLEY, Material.FLOWER_POT);
        map.put(Material.POTTED_WITHER_ROSE, Material.FLOWER_POT);
        map.put(Material.POTTED_RED_MUSHROOM, Material.FLOWER_POT);
        map.put(Material.POTTED_BROWN_MUSHROOM, Material.FLOWER_POT);
        map.put(Material.POTTED_DEAD_BUSH, Material.FLOWER_POT);
        map.put(Material.POTTED_CACTUS, Material.FLOWER_POT);
        map.put(Material.CARROTS, Material.CARROT);
        map.put(Material.POTATOES, Material.POTATO);
        map.put(Material.SKELETON_WALL_SKULL, Material.SKELETON_SKULL);
        map.put(Material.WITHER_SKELETON_WALL_SKULL, Material.WITHER_SKELETON_SKULL);
        map.put(Material.ZOMBIE_WALL_HEAD, Material.ZOMBIE_HEAD);
        map.put(Material.PLAYER_WALL_HEAD, Material.PLAYER_HEAD);
        map.put(Material.CREEPER_WALL_HEAD, Material.CREEPER_HEAD);
        map.put(Material.DRAGON_WALL_HEAD, Material.DRAGON_HEAD);
        map.put(Material.WHITE_WALL_BANNER, Material.WHITE_BANNER);
        map.put(Material.ORANGE_WALL_BANNER, Material.ORANGE_BANNER);
        map.put(Material.MAGENTA_WALL_BANNER, Material.MAGENTA_BANNER);
        map.put(Material.LIGHT_BLUE_WALL_BANNER, Material.LIGHT_BLUE_BANNER);
        map.put(Material.YELLOW_WALL_BANNER, Material.YELLOW_BANNER);
        map.put(Material.LIME_WALL_BANNER, Material.LIME_BANNER);
        map.put(Material.PINK_WALL_BANNER, Material.PINK_BANNER);
        map.put(Material.GRAY_WALL_BANNER, Material.GRAY_BANNER);
        map.put(Material.LIGHT_GRAY_WALL_BANNER, Material.LIGHT_GRAY_BANNER);
        map.put(Material.CYAN_WALL_BANNER, Material.CYAN_BANNER);
        map.put(Material.PURPLE_WALL_BANNER, Material.PURPLE_BANNER);
        map.put(Material.BLUE_WALL_BANNER, Material.BLUE_BANNER);
        map.put(Material.BROWN_WALL_BANNER, Material.BROWN_BANNER);
        map.put(Material.GREEN_WALL_BANNER, Material.GREEN_BANNER);
        map.put(Material.RED_WALL_BANNER, Material.RED_BANNER);
        map.put(Material.BLACK_WALL_BANNER, Material.BLACK_BANNER);
        map.put(Material.BEETROOTS, Material.BEETROOT);
        map.put(Material.END_GATEWAY, Material.END_PORTAL_FRAME);
        map.put(Material.FROSTED_ICE, Material.ICE);
        map.put(Material.KELP_PLANT, Material.KELP);
        map.put(Material.DEAD_TUBE_CORAL_WALL_FAN, Material.DEAD_TUBE_CORAL_FAN);
        map.put(Material.DEAD_BRAIN_CORAL_WALL_FAN, Material.DEAD_BRAIN_CORAL_FAN);
        map.put(Material.DEAD_BUBBLE_CORAL_WALL_FAN, Material.DEAD_BUBBLE_CORAL_FAN);
        map.put(Material.DEAD_FIRE_CORAL_WALL_FAN, Material.DEAD_FIRE_CORAL_FAN);
        map.put(Material.DEAD_HORN_CORAL_WALL_FAN, Material.DEAD_HORN_CORAL_FAN);
        map.put(Material.TUBE_CORAL_WALL_FAN, Material.TUBE_CORAL_FAN);
        map.put(Material.BRAIN_CORAL_WALL_FAN, Material.BRAIN_CORAL_FAN);
        map.put(Material.BUBBLE_CORAL_WALL_FAN, Material.BUBBLE_CORAL_FAN);
        map.put(Material.FIRE_CORAL_WALL_FAN, Material.FIRE_CORAL_FAN);
        map.put(Material.HORN_CORAL_WALL_FAN, Material.HORN_CORAL_FAN);
        map.put(Material.BAMBOO_SAPLING, Material.BAMBOO);
        map.put(Material.POTTED_BAMBOO, Material.FLOWER_POT);
        map.put(Material.VOID_AIR, Material.GLASS);
        map.put(Material.CAVE_AIR, Material.GLASS);
        map.put(Material.BUBBLE_COLUMN, Material.WATER_BUCKET);
        map.put(Material.SWEET_BERRY_BUSH, Material.SWEET_BERRIES);
        map.put(Material.WEEPING_VINES_PLANT, Material.WEEPING_VINES);
        map.put(Material.TWISTING_VINES_PLANT, Material.TWISTING_VINES);
        map.put(Material.CRIMSON_WALL_SIGN, Material.CRIMSON_SIGN);
        map.put(Material.WARPED_WALL_SIGN, Material.WARPED_SIGN);
        map.put(Material.POTTED_CRIMSON_FUNGUS, Material.FLOWER_POT);
        map.put(Material.POTTED_WARPED_FUNGUS, Material.FLOWER_POT);
        map.put(Material.POTTED_CRIMSON_ROOTS, Material.FLOWER_POT);
        map.put(Material.POTTED_WARPED_ROOTS, Material.FLOWER_POT);
        map.put(Material.CANDLE_CAKE, Material.CANDLE);
        map.put(Material.WHITE_CANDLE_CAKE, Material.WHITE_CANDLE);
        map.put(Material.ORANGE_CANDLE_CAKE, Material.ORANGE_CANDLE);
        map.put(Material.MAGENTA_CANDLE_CAKE, Material.MAGENTA_CANDLE);
        map.put(Material.LIGHT_BLUE_CANDLE_CAKE, Material.LIGHT_BLUE_CANDLE);
        map.put(Material.YELLOW_CANDLE_CAKE, Material.YELLOW_CANDLE);
        map.put(Material.LIME_CANDLE_CAKE, Material.LIME_CANDLE);
        map.put(Material.PINK_CANDLE_CAKE, Material.PINK_CANDLE);
        map.put(Material.GRAY_CANDLE_CAKE, Material.GRAY_CANDLE);
        map.put(Material.LIGHT_GRAY_CANDLE_CAKE, Material.LIGHT_GRAY_CANDLE);
        map.put(Material.CYAN_CANDLE_CAKE, Material.CYAN_CANDLE);
        map.put(Material.PURPLE_CANDLE_CAKE, Material.PURPLE_CANDLE);
        map.put(Material.BLUE_CANDLE_CAKE, Material.BLUE_CANDLE);
        map.put(Material.BROWN_CANDLE_CAKE, Material.BROWN_CANDLE);
        map.put(Material.GREEN_CANDLE_CAKE, Material.GREEN_CANDLE);
        map.put(Material.RED_CANDLE_CAKE, Material.RED_CANDLE);
        map.put(Material.BLACK_CANDLE_CAKE, Material.BLACK_CANDLE);
        map.put(Material.POWDER_SNOW, Material.SNOWBALL);
        map.put(Material.CAVE_VINES, Material.GLOW_BERRIES);
        map.put(Material.CAVE_VINES_PLANT, Material.GLOW_BERRIES);
        map.put(Material.BIG_DRIPLEAF_STEM, Material.BIG_DRIPLEAF);
        map.put(Material.POTTED_AZALEA_BUSH, Material.FLOWER_POT);
        map.put(Material.POTTED_FLOWERING_AZALEA_BUSH, Material.FLOWER_POT);
    }

    public static void fillEntityTypeMaterial(HashMap<EntityType, Material> map) {
        map.put(EntityType.EXPERIENCE_ORB, Material.EXPERIENCE_BOTTLE);
        map.put(EntityType.AREA_EFFECT_CLOUD, Material.POTION);
        map.put(EntityType.ELDER_GUARDIAN, Material.ELDER_GUARDIAN_SPAWN_EGG);
        map.put(EntityType.WITHER_SKELETON, Material.WITHER_SKELETON_SPAWN_EGG);
        map.put(EntityType.STRAY, Material.STRAY_SPAWN_EGG);
        map.put(EntityType.EGG, Material.EGG);
        map.put(EntityType.LEASH_HITCH, Material.LEAD);
        map.put(EntityType.PAINTING, Material.PAINTING);
        map.put(EntityType.ARROW, Material.ARROW);
        map.put(EntityType.SNOWBALL, Material.SNOWBALL);
        map.put(EntityType.FIREBALL, Material.GHAST_SPAWN_EGG);
        map.put(EntityType.SMALL_FIREBALL, Material.FIRE_CHARGE);
        map.put(EntityType.ENDER_PEARL, Material.ENDER_PEARL);
        map.put(EntityType.ENDER_SIGNAL, Material.ENDER_EYE);
        map.put(EntityType.THROWN_EXP_BOTTLE, Material.EXPERIENCE_BOTTLE);
        map.put(EntityType.ITEM_FRAME, Material.ITEM_FRAME);
        map.put(EntityType.WITHER_SKULL, Material.WITHER_SKELETON_SKULL);
        map.put(EntityType.PRIMED_TNT, Material.TNT);
        map.put(EntityType.HUSK, Material.HUSK_SPAWN_EGG);
        map.put(EntityType.SPECTRAL_ARROW, Material.SPECTRAL_ARROW);
        map.put(EntityType.SHULKER_BULLET, Material.SHULKER_SHELL);
        map.put(EntityType.DRAGON_FIREBALL, Material.DRAGON_BREATH);
        map.put(EntityType.ZOMBIE_VILLAGER, Material.ZOMBIE_VILLAGER_SPAWN_EGG);
        map.put(EntityType.SKELETON_HORSE, Material.SKELETON_HORSE_SPAWN_EGG);
        map.put(EntityType.ZOMBIE_HORSE, Material.ZOMBIE_HORSE_SPAWN_EGG);
        map.put(EntityType.ARMOR_STAND, Material.ARMOR_STAND);
        map.put(EntityType.DONKEY, Material.DONKEY_SPAWN_EGG);
        map.put(EntityType.MULE, Material.MULE_SPAWN_EGG);
        map.put(EntityType.EVOKER_FANGS, Material.EVOKER_SPAWN_EGG);
        map.put(EntityType.EVOKER, Material.EVOKER_SPAWN_EGG);
        map.put(EntityType.VEX, Material.VEX_SPAWN_EGG);
        map.put(EntityType.VINDICATOR, Material.VINDICATOR_SPAWN_EGG);
        map.put(EntityType.ILLUSIONER, Material.COMMAND_BLOCK);
        map.put(EntityType.MINECART_COMMAND, Material.COMMAND_BLOCK_MINECART);
        map.put(EntityType.BOAT, Material.OAK_BOAT);
        map.put(EntityType.MINECART, Material.MINECART);
        map.put(EntityType.MINECART_CHEST, Material.CHEST_MINECART);
        map.put(EntityType.MINECART_FURNACE, Material.FURNACE_MINECART);
        map.put(EntityType.MINECART_TNT, Material.TNT_MINECART);
        map.put(EntityType.MINECART_HOPPER, Material.HOPPER_MINECART);
        map.put(EntityType.MINECART_MOB_SPAWNER, Material.SPAWNER);
        map.put(EntityType.CREEPER, Material.CREEPER_SPAWN_EGG);
        map.put(EntityType.SKELETON, Material.SKELETON_SPAWN_EGG);
        map.put(EntityType.SPIDER, Material.SPIDER_SPAWN_EGG);
        map.put(EntityType.GIANT, Material.ZOMBIE_SPAWN_EGG);
        map.put(EntityType.ZOMBIE, Material.ZOMBIE_SPAWN_EGG);
        map.put(EntityType.SLIME, Material.SLIME_SPAWN_EGG);
        map.put(EntityType.GHAST, Material.GHAST_SPAWN_EGG);
        map.put(EntityType.ZOMBIFIED_PIGLIN, Material.ZOMBIFIED_PIGLIN_SPAWN_EGG);
        map.put(EntityType.ENDERMAN, Material.ENDERMAN_SPAWN_EGG);
        map.put(EntityType.CAVE_SPIDER, Material.CAVE_SPIDER_SPAWN_EGG);
        map.put(EntityType.SILVERFISH, Material.SILVERFISH_SPAWN_EGG);
        map.put(EntityType.BLAZE, Material.BLAZE_SPAWN_EGG);
        map.put(EntityType.MAGMA_CUBE, Material.MAGMA_CUBE_SPAWN_EGG);
        map.put(EntityType.ENDER_DRAGON, Material.COMMAND_BLOCK);
        map.put(EntityType.WITHER, Material.COMMAND_BLOCK);
        map.put(EntityType.BAT, Material.BAT_SPAWN_EGG);
        map.put(EntityType.WITCH, Material.WITCH_SPAWN_EGG);
        map.put(EntityType.ENDERMITE, Material.ENDERMITE_SPAWN_EGG);
        map.put(EntityType.GUARDIAN, Material.GUARDIAN_SPAWN_EGG);
        map.put(EntityType.SHULKER, Material.SHULKER_SPAWN_EGG);
        map.put(EntityType.PIG, Material.PIG_SPAWN_EGG);
        map.put(EntityType.SHEEP, Material.SHEEP_SPAWN_EGG);
        map.put(EntityType.COW, Material.COW_SPAWN_EGG);
        map.put(EntityType.CHICKEN, Material.CHICKEN_SPAWN_EGG);
        map.put(EntityType.SQUID, Material.SQUID_SPAWN_EGG);
        map.put(EntityType.WOLF, Material.WOLF_SPAWN_EGG);
        map.put(EntityType.MUSHROOM_COW, Material.MOOSHROOM_SPAWN_EGG);
        map.put(EntityType.SNOWMAN, Material.CARVED_PUMPKIN);
        map.put(EntityType.OCELOT, Material.OCELOT_SPAWN_EGG);
        map.put(EntityType.IRON_GOLEM, Material.CARVED_PUMPKIN);
        map.put(EntityType.HORSE, Material.HORSE_SPAWN_EGG);
        map.put(EntityType.RABBIT, Material.RABBIT_SPAWN_EGG);
        map.put(EntityType.POLAR_BEAR, Material.POLAR_BEAR_SPAWN_EGG);
        map.put(EntityType.LLAMA, Material.LLAMA_SPAWN_EGG);
        map.put(EntityType.LLAMA_SPIT, Material.LLAMA_SPAWN_EGG);
        map.put(EntityType.PARROT, Material.PARROT_SPAWN_EGG);
        map.put(EntityType.VILLAGER, Material.VILLAGER_SPAWN_EGG);
        map.put(EntityType.ENDER_CRYSTAL, Material.END_CRYSTAL);
        map.put(EntityType.TURTLE, Material.TURTLE_SPAWN_EGG);
        map.put(EntityType.PHANTOM, Material.PHANTOM_SPAWN_EGG);
        map.put(EntityType.TRIDENT, Material.TRIDENT);
        map.put(EntityType.COD, Material.COD_SPAWN_EGG);
        map.put(EntityType.SALMON, Material.SALMON_SPAWN_EGG);
        map.put(EntityType.PUFFERFISH, Material.PUFFERFISH_SPAWN_EGG);
        map.put(EntityType.TROPICAL_FISH, Material.TROPICAL_FISH_SPAWN_EGG);
        map.put(EntityType.DROWNED, Material.DROWNED_SPAWN_EGG);
        map.put(EntityType.DOLPHIN, Material.DOLPHIN_SPAWN_EGG);
        map.put(EntityType.CAT, Material.CAT_SPAWN_EGG);
        map.put(EntityType.PANDA, Material.PANDA_SPAWN_EGG);
        map.put(EntityType.PILLAGER, Material.PILLAGER_SPAWN_EGG);
        map.put(EntityType.RAVAGER, Material.RAVAGER_SPAWN_EGG);
        map.put(EntityType.TRADER_LLAMA, Material.TRADER_LLAMA_SPAWN_EGG);
        map.put(EntityType.WANDERING_TRADER, Material.WANDERING_TRADER_SPAWN_EGG);
        map.put(EntityType.FOX, Material.FOX_SPAWN_EGG);
        map.put(EntityType.BEE, Material.BEE_SPAWN_EGG);
        map.put(EntityType.HOGLIN, Material.HOGLIN_SPAWN_EGG);
        map.put(EntityType.PIGLIN, Material.PIGLIN_SPAWN_EGG);
        map.put(EntityType.STRIDER, Material.STRIDER_SPAWN_EGG);
        map.put(EntityType.ZOGLIN, Material.ZOGLIN_SPAWN_EGG);
        map.put(EntityType.PIGLIN_BRUTE, Material.PIGLIN_BRUTE_SPAWN_EGG);
        map.put(EntityType.AXOLOTL, Material.AXOLOTL_SPAWN_EGG);
        map.put(EntityType.GLOW_ITEM_FRAME, Material.GLOW_ITEM_FRAME);
        map.put(EntityType.GLOW_SQUID, Material.GLOW_SQUID_SPAWN_EGG);
        map.put(EntityType.GOAT, Material.GOAT_SPAWN_EGG);
        map.put(EntityType.MARKER, Material.MAP);
        map.put(EntityType.ALLAY, Material.ALLAY_SPAWN_EGG);
        map.put(EntityType.CHEST_BOAT, Material.OAK_CHEST_BOAT);
        map.put(EntityType.FROG, Material.FROG_SPAWN_EGG);
        map.put(EntityType.TADPOLE, Material.TADPOLE_SPAWN_EGG);
        map.put(EntityType.WARDEN, Material.WARDEN_SPAWN_EGG);
    }
}
