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
        return Set.copyOf(result);
    }

    @Override
    public @NotNull Collection<T> values() {
        Collection<T> result = new ArrayList<>();
        for (Map<BlockVector, T> innerMap : outer.values()) {
            result.addAll(innerMap.values());
        }
        return result;
    }

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
        return Set.copyOf(set);
    }
}
