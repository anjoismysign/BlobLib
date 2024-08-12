package us.mytheria.bloblib.placeholderapi;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.enginehub.EngineHubManager;
import us.mytheria.bloblib.enginehub.worldedit.WorldEditWorker;
import us.mytheria.bloblib.entities.BlobPHExpansion;
import us.mytheria.bloblib.entities.area.WorldGuardArea;
import us.mytheria.bloblib.entities.translatable.TranslatableArea;

import java.util.Iterator;
import java.util.Objects;

public class WorldGuardPH {
    private static WorldGuardPH instance;
    private BlobPHExpansion expansion;

    @NotNull
    public static WorldGuardPH getInstance(@NotNull BlobLib plugin) {
        if (instance == null) {
            Objects.requireNonNull(plugin, "injected dependency is null");
            instance = new WorldGuardPH(plugin);
        }
        return instance;
    }

    private WorldGuardPH(@NotNull BlobLib plugin) {
        instance = this;
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            BlobLib.getAnjoLogger().log("PlaceholderAPI not found, not registering WorldGuard PlaceholderAPI expansion");
            return;
        }
        BlobPHExpansion expansion = new BlobPHExpansion(plugin, "worldguard");
        this.expansion = expansion;
        expansion.putSimple("translatable_area", offlinePlayer -> {
            Player player = Bukkit.getPlayer(offlinePlayer.getUniqueId());
            if (player == null)
                return ChatColor.RED + "Offline";
            ProtectedRegion region = getRegion(player.getLocation());
            if (region == null)
                return BlobLibTranslatableAPI.getInstance().getTranslatableSnippet("BlobLib.Wilderness", player).get();
            WorldGuardArea worldGuardArea = null;
            TranslatableArea translatableArea = null;
            for (TranslatableArea translatable : BlobLibTranslatableAPI.getInstance().getTranslatableAreas(player.getLocale())) {
                if (!(translatable.get() instanceof WorldGuardArea area))
                    continue;
                if (!area.getId().equals(region.getId()))
                    continue;
                worldGuardArea = area;
                translatableArea = translatable;
                break;
            }
            if (worldGuardArea == null)
                return BlobLibTranslatableAPI.getInstance().getTranslatableSnippet("BlobLib.Wilderness", player).get();
            return translatableArea.localize(player).getDisplay();
        });
    }

    @Nullable
    private ProtectedRegion getRegion(Location location) {
        if (location == null)
            return null;
        WorldEditWorker worldEditWorker = EngineHubManager.getInstance().getWorldEditWorker();
        Object blockVector3Object = worldEditWorker.blockVector3(location);
        if (blockVector3Object == null)
            return null;
        BlockVector3 blockVector3 = (BlockVector3) blockVector3Object;
        Object regionManagerObject = EngineHubManager.getInstance().getWorldGuardWorker().regionManager(location.getWorld());
        if (regionManagerObject == null)
            return null;
        RegionManager regionManager = (RegionManager) regionManagerObject;
        ApplicableRegionSet applicableRegionSet = regionManager.getApplicableRegions(blockVector3, RegionQuery.QueryOption.SORT);
        Iterator<ProtectedRegion> iterator = applicableRegionSet.getRegions().iterator();
        if (!iterator.hasNext())
            return null;
        return iterator.next();
    }

    public BlobPHExpansion getExpansion() {
        return expansion;
    }
}
