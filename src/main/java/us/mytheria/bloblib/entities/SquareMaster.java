package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Tuple2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SquareMaster<T> {
    private double width, height;
    private int rows, maxPerRow;
    private final Map<Integer, T> components;
    private final Map<Integer, Integer> indexes;
    private double componentLength;

    public SquareMaster(int maxPerRow,
                        double componentLength,
                        @NotNull Map<Integer, T> components) {
        this.components = components;
        this.indexes = new HashMap<>();
        this.maxPerRow = maxPerRow;
        this.componentLength = componentLength;
    }

    private void reload() {
        int components = this.components.size();
        rows = (int) Math.ceil((double) components / maxPerRow);
        width = Math.min(components, maxPerRow) * componentLength;
        height = rows * componentLength;
    }

    /**
     * Merges the indexes with the master
     *
     * @param indexes - the indexes
     */
    public void mergeIndexes(Map<Integer, Integer> indexes) {
        this.indexes.putAll(indexes);
    }

    public void setComponentLength(double componentLength) {
        this.componentLength = componentLength;
        reload();
    }

    public void setMaxPerRow(int maxPerRow) {
        this.maxPerRow = maxPerRow;
    }

    public int getMaxPerRow() {
        return maxPerRow;
    }

    /**
     * Adds a component to the master at the specified index (real index)
     *
     * @param component - the component
     * @param realIndex - the real index
     * @return the index the component was added at
     */
    public int addComponent(T component, int realIndex) {
        components.put(realIndex, component);
        int lowestMissing = getLowestMissing(indexes.keySet());
        indexes.put(lowestMissing, realIndex);
        reload();
        return lowestMissing;
    }

    private int getLowestMissing(Set<Integer> indexes) {
        int i = 0;
        while (indexes.contains(i))
            i++;
        return i;
    }

    /**
     * Removes a component from the master at the specified index (real index)
     *
     * @param index - the index
     */
    public void removeComponent(int index) {
        Integer real = indexes.get(index);
        if (real == null)
            return;
        components.remove(real);
        indexes.remove(index);
        reload();
    }

    /**
     * Gets the component at the specified index
     *
     * @param index - the index
     * @return the component
     */
    @Nullable
    public Tuple2<Integer, T> getComponent(int index) {
        Integer real = indexes.get(index);
        if (real == null)
            return null;
        T component = components.get(real);
        if (component == null)
            return null;
        return new Tuple2<>(real, component);
    }

    /**
     * Gets the index of the specified component
     *
     * @param realIndex - the real index
     * @return the index
     */
    public Integer getIndex(int realIndex) {
        return indexes.entrySet().stream()
                .filter(entry -> entry.getValue().equals(realIndex))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public Vector2d getOffset(int index) {
        int row = getRow(index);
        int column = getColumn(index);
        double componentLength = getComponentLength();
        double x = column + (componentLength / 2);
        double z = row + (componentLength / 2);
        return new Vector2d(x, z);
    }

    public double getComponentLength() {
        return componentLength;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public int getRows() {
        return rows;
    }

    public int getRow(int index) {
        return index / maxPerRow;
    }

    public int getColumn(int index) {
        return index % maxPerRow;
    }

    /**
     * Gets an unmodifiable copy of the map of the indexes.
     *
     * @return the indexes
     */
    public Map<Integer, Integer> getIndexes() {
        return Map.copyOf(indexes);
    }
}
