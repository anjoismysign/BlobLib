package io.github.anjoismysign.bloblib.managers.map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A specialized {@link Map} implementation that uses Bukkit {@link Block} instances
 * as keys, storing values in a two-level structure: first by world name, then by
 * block coordinates (as {@link BlockVector}). This allows efficient lookup, insertion,
 * and removal of values associated with specific blocks across multiple worlds.
 *
 * @param <T> the type of values stored in this map
 */
public class BlockMap<T> implements Map<Block, T> {

    private final Map<String, Map<BlockVector, T>> outer = new HashMap<>();

    @Override
    public int size() {
        int size = 0;
        for (Entry<String, Map<BlockVector, T>> entry : outer.entrySet()) {
            Map<BlockVector, T> value = entry.getValue();
            size += value.size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Entry<String, Map<BlockVector, T>> entry : outer.entrySet()) {
            Map<BlockVector, T> inner = entry.getValue();
            if (inner.containsValue(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public T get(Object key) {
        if (!(key instanceof Block block)) {
            return null;
        }
        String worldName = block.getWorld().getName();
        BlockVector vector = block.getLocation().toVector().toBlockVector();
        @Nullable Map<BlockVector, T> inner = outer.get(worldName);
        if (inner == null) {
            return null;
        }
        return inner.get(vector);
    }

    @Override
    public @Nullable T put(Block key, T value) {
        String worldName = key.getWorld().getName();
        BlockVector vector = key.getLocation().toVector().toBlockVector();
        return outer.computeIfAbsent(worldName, w -> new HashMap<>()).put(vector, value);
    }

    @Override
    public T remove(Object key) {
        if (!(key instanceof Block block)) {
            return null;
        }
        String worldName = block.getWorld().getName();
        BlockVector vector = block.getLocation().toVector().toBlockVector();
        @Nullable Map<BlockVector, T> inner = outer.get(worldName);
        if (inner == null) {
            return null;
        }
        return inner.remove(vector);
    }

    @Override
    public void putAll(@NotNull Map<? extends Block, ? extends T> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        outer.clear();
    }

    /**
     * Returns a {@link Set} view of the {@link Block} keys contained in this map.
     * The set is a snapshot copy and is not backed by the map.
     *
     * @return a set of all blocks that have mappings in this map
     */
    @Override
    public @NotNull Set<Block> keySet() {
        Set<Block> result = new HashSet<>();
        outer.forEach((worldName, inner) -> {
            @Nullable World world = Bukkit.getWorld(worldName);
            if (world == null) {
                return;
            }
            Set<BlockVector> innerSet = inner.keySet();
            innerSet.forEach(vector -> {
                Block block = world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
                result.add(block);
            });
        });
        return result;
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is a snapshot copy and is not backed by the map.
     *
     * @return a collection of all mapped values
     */
    @Override
    public @NotNull Collection<T> values() {
        Collection<T> result = new ArrayList<>();
        for (Map<BlockVector, T> innerMap : outer.values()) {
            result.addAll(innerMap.values());
        }
        return result;
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * Each entry maps a {@link Block} to its associated value.
     * The set is a snapshot copy and is not backed by the map.
     *
     * @return a set of block-value entries
     */
    @Override
    public @NotNull Set<Entry<Block, T>> entrySet() {
        Set<Entry<Block, T>> set = new HashSet<>();
        for (Entry<String, Map<BlockVector, T>> entry : outer.entrySet()) {
            String worldName = entry.getKey();
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                continue;
            }
            Map<BlockVector, T> inner = entry.getValue();
            inner.forEach(((vector, instance) -> {
                Block block = world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
                Entry<Block, T> mapEntry = Map.entry(block, instance);
                set.add(mapEntry);
            }));
        }
        return set;
    }
}
