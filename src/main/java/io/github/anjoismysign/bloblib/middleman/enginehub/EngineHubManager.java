package io.github.anjoismysign.bloblib.middleman.enginehub;

import io.github.anjoismysign.bloblib.middleman.enginehub.worldedit.Absent;
import io.github.anjoismysign.bloblib.middleman.enginehub.worldedit.Found;
import io.github.anjoismysign.bloblib.middleman.enginehub.worldedit.WorldEditWorker;
import io.github.anjoismysign.bloblib.middleman.enginehub.worldguard.WorldGuardWorker;
import org.bukkit.Bukkit;

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
            worldGuard = new io.github.anjoismysign.bloblib.middleman.enginehub.worldguard.Absent();
        } else {
            worldGuard = new io.github.anjoismysign.bloblib.middleman.enginehub.worldguard.Found(worldEdit);
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
