package io.github.anjoismysign.bloblib.listeners;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.ProjectileDamageAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.Nullable;

public class ProjectileDamage implements Listener {

    public ProjectileDamage() {
        Bukkit.getPluginManager().registerEvents(this, BlobLib.getInstance());
    }

    @EventHandler(ignoreCancelled = true)
    public void onShoot(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        @Nullable ProjectileSource shooter = projectile.getShooter();
        if (shooter == null)
            return;
        if (!(projectile instanceof Arrow arrow))
            return;
        if (!(shooter instanceof LivingEntity livingEntity))
            return;
        ItemStack activeItem = livingEntity.getActiveItem();
        @Nullable ItemMeta itemMeta = activeItem.getItemMeta();
        if (itemMeta == null)
            return;
        ProjectileDamageAPI api = ProjectileDamageAPI.getInstance();
        if (!api.isInstance(activeItem))
            return;
        double damage = api.get(itemMeta);
        arrow.setDamage(damage);
    }
}
