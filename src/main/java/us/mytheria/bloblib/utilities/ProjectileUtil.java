package us.mytheria.bloblib.utilities;

import org.bukkit.entity.EntityType;

import java.util.HashSet;
import java.util.Set;

public class ProjectileUtil {
    private static final Set<String> projectiles;

    static {
        projectiles = new HashSet<>();
        projectiles.add("ARROW");
        projectiles.add("DRAGON_FIREBALL");
        projectiles.add("EGG");
        projectiles.add("ENDER_PEARL");
        projectiles.add("FIREBALL");
        projectiles.add("FIREWORK");
        projectiles.add("FISHING_HOOK");
        projectiles.add("SPLASH_POTION");
        projectiles.add("LLAMA_SPIT");
        projectiles.add("SHULKER_BULLET");
        projectiles.add("SNOWBALL");
        projectiles.add("SPECTRAL_ARROW");
        projectiles.add("THROWN_EXP_BOTTLE");
        projectiles.add("TRIDENT");
        projectiles.add("WITHER_SKULL");
    }

    /**
     * Returns whether the given entity type is a projectile.
     *
     * @param entityType the entity type to check
     * @return whether the given entity type is a projectile
     */
    public static boolean isProjectile(EntityType entityType) {
        String name = entityType.name();
        return projectiles.contains(name);
    }
}
