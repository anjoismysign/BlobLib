package us.mytheria.bloblib.utilities;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.LinkedList;

public class WorldUtil {
    public LinkedList<Chunk> getSpawnChunks(World world) {
        LinkedList<Chunk> spawnChunks = new LinkedList<>();
        Location spawnLocation = world.getSpawnLocation();

        int spawnChunkX = spawnLocation.getBlockX() >> 4;
        int spawnChunkZ = spawnLocation.getBlockZ() >> 4;

        for (int x = spawnChunkX - 11; x <= spawnChunkX + 11; x++) {
            for (int z = spawnChunkZ - 11; z <= spawnChunkZ + 11; z++) {
                Chunk chunk = world.getChunkAt(x, z);
                spawnChunks.add(chunk);
            }
        }
        return spawnChunks;
    }
}
