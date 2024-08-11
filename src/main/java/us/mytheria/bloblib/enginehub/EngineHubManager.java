package us.mytheria.bloblib.enginehub;

import org.bukkit.Bukkit;
import us.mytheria.bloblib.enginehub.worldedit.Absent;
import us.mytheria.bloblib.enginehub.worldedit.Found;
import us.mytheria.bloblib.enginehub.worldedit.WorldEditWorker;
import us.mytheria.bloblib.enginehub.worldguard.WorldGuardWorker;

public class EngineHubManager {
    private final WorldEditWorker worldEdit;
    private boolean worldEditInstalled = false;
    private final WorldGuardWorker worldGuard;
    private boolean worldGuardInstalled = false;

    private static EngineHubManager instance;

    public static EngineHubManager getInstance() {
        if (instance == null)
            instance = new EngineHubManager();
        return instance;
    }

    private EngineHubManager() {
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            Bukkit.getLogger().info("[BlobLib] WorldEdit not found, disabling its features.");
            worldEdit = new Absent();
        } else {
            worldEdit = new Found();
            worldEditInstalled = true;
        }
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            Bukkit.getLogger().info("[BlobLib] WorldGuard not found, disabling its features.");
            worldGuard = new us.mytheria.bloblib.enginehub.worldguard.Absent();
        } else {
            worldGuard = new us.mytheria.bloblib.enginehub.worldguard.Found(worldEdit);
            worldGuardInstalled = true;
        }
    }

    public WorldEditWorker getWorldEditWorker() {
        return worldEdit;
    }

    public WorldGuardWorker getWorldGuardWorker() {
        return worldGuard;
    }

    public boolean isWorldEditInstalled() {
        return worldEditInstalled;
    }

    public boolean isWorldGuardInstalled() {
        return worldGuardInstalled;
    }
}
