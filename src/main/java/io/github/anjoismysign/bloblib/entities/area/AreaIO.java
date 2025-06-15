package io.github.anjoismysign.bloblib.entities.area;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.enginehub.EngineHubManager;
import io.github.anjoismysign.bloblib.entities.translatable.BlobTranslatableArea;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableArea;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import io.github.anjoismysign.bloblib.managers.BlobLibListenerManager;
import io.github.anjoismysign.bloblib.utilities.SerializationLib;
import io.github.anjoismysign.bloblib.utilities.VectorUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public enum AreaIO {
    INSTANCE;

    /**
     * Serializes a Location randomly
     *
     * @param player The player in context
     * @return The randomly generated reference
     */
    @Nullable
    public String writeRandom(@NotNull Player player) {
        Objects.requireNonNull(player, "'selector' cannot be null");
        @Nullable BoundingBox boundingBox = BlobLibListenerManager.getInstance().getAreaWand().has(player);
        if (boundingBox == null)
            return null;
        World world = player.getWorld();

        File directory = BlobLib.getInstance().getTranslatableAreaManager().getAssetDirectory();
        UUID random = UUID.randomUUID();
        String key = random.toString();
        BlobTranslatableArea translatableArea = BlobTranslatableArea.of(
                key,
                "en_us",
                "Change me later!",
                new BoxArea(boundingBox, world.getName(), null)
        );
        File file = new File(directory, key + ".yml");
        directory.mkdirs();
        try {
            file.createNewFile();
        } catch ( IOException exception ) {
            throw new RuntimeException(exception);
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        write(configuration, translatableArea);
        try {
            configuration.save(file);
        } catch ( IOException exception ) {
            throw new RuntimeException(exception);
        }
        return key;
    }

    public void write(@NotNull ConfigurationSection at,
                      @NotNull TranslatableArea translatableArea) {
        Objects.requireNonNull(translatableArea, "'translatableArea' cannot be null");
        Area area = translatableArea.get();
        if (!(area instanceof BoxArea boxArea))
            return;
        String locale = translatableArea.locale();
        String display = translatableArea.getDisplay();

        at.set("Locale", locale);
        at.set("Display", display);
        write(at, boxArea);

    }

    public void write(@NotNull ConfigurationSection at,
                      @NotNull BoxArea area) {
        Objects.requireNonNull(at, "'at' cannot be null");
        Objects.requireNonNull(area, "'area' cannot be null");

        BoundingBox boundingBox = area.getBoundingBox();
        Vector min = boundingBox.getMin();
        Vector max = boundingBox.getMax();
        @Nullable Location center = area.getCenter();

        VectorUtil.setVector(min, at.createSection("Min"));
        VectorUtil.setVector(max, at.createSection("Max"));

        at.set("World", area.getWorldName());
        if (center == null)
            return;
        at.set("Center", SerializationLib.serialize(center.toVector().toBlockVector()));
    }

    @NotNull
    public Area read(@NotNull ConfigurationSection section) {
        Objects.requireNonNull(section, "'section' cannot be null");
        if (!section.isString("World"))
            throw new ConfigurationFieldException("'World' is missing or not set");
        String worldName = section.getString("World");
        @Nullable String id = section.getString("Id");
        boolean worldGuard = EngineHubManager.getInstance().isWorldGuardInstalled();
        @Nullable ConfigurationSection minSection = section.getConfigurationSection("Min");
        @Nullable ConfigurationSection maxSection = section.getConfigurationSection("Max");
        BlockVector center = null;
        try {
            center = SerializationLib.deserializeBlockVector(section.getString("Center"));
        } catch ( Exception ignored ) {
        }
        Area area;
        if (minSection != null && maxSection != null) {
            if (id != null)
                throw new ConfigurationFieldException("'Id' is present while 'Min' and 'Max' are as well");
            Vector min = VectorUtil.fromConfigurationSection(minSection);
            Vector max = VectorUtil.fromConfigurationSection(maxSection);
            BoundingBox boundingBox = BoundingBox.of(min, max);
            area = new BoxArea(boundingBox, worldName, center);
            return area;
        }
        if (id != null) {
            if (!worldGuard)
                throw new RuntimeException("While attempting to load a TranslatableArea, WorldGuard seemed to not be installed");
            area = WorldGuardArea.of(worldName, id, center);
            return area;
        } else
            throw new ConfigurationFieldException("'Id' is null while missing both 'Min' and 'Max'");
    }
}
