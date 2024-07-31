package us.mytheria.bloblib.hologram;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class FancyHolograms implements HologramDriver {

    @Override
    public void create(String name, Location location, List<String> lines) {
        create(name, location, lines, false);
    }

    @Override
    public void create(String name,
                       Location location,
                       List<String> lines,
                       boolean saveToConfig) {
        create(name,
                location,
                lines,
                saveToConfig,
                false,
                Color.fromARGB(0, 0, 0, 0),
                null,
                null,
                false,
                -1,
                0.0F,
                1.0F,
                null,
                null,
                null);
    }

    public void create(String name,
                       Location location,
                       List<String> lines,
                       boolean saveToConfig,
                       boolean seeThrough,
                       @Nullable Color background,
                       @Nullable Display.Billboard billboard,
                       @Nullable TextDisplay.TextAlignment textAlignment,
                       boolean textShadow,
                       int textUpdateInterval,
                       float shadowRadius,
                       float shadowStrength,
                       @Nullable Display.Brightness brightness,
                       @Nullable Vector3f scale,
                       @Nullable Vector3f translation) {
        HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
        TextHologramData hologramData = new TextHologramData(name, location);
        if (scale != null)
            hologramData.setScale(scale);
        if (translation != null)
            hologramData.setTranslation(translation);
        if (brightness != null)
            hologramData.setBrightness(brightness);
        hologramData.setShadowRadius(shadowRadius);
        hologramData.setShadowStrength(shadowStrength);
        hologramData.setTextUpdateInterval(textUpdateInterval);
        hologramData.setTextShadow(textShadow);
        if (textAlignment != null)
            hologramData.setTextAlignment(textAlignment);
        if (billboard != null)
            hologramData.setBillboard(billboard);
        hologramData.setSeeThrough(seeThrough);
        if (background != null)
            hologramData.setBackground(background);
        hologramData.setPersistent(saveToConfig);
        hologramData.setText(lines);
        Hologram hologram = manager.create(hologramData);
        manager.addHologram(hologram);
    }

    @Nullable
    public Hologram get(String name) {
        HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
        return manager.getHologram(name).orElse(null);
    }

    @Override
    public void update(String name) {
        Hologram hologram = get(name);
        if (hologram == null)
            return;
        hologram.refreshForViewers();
    }

    @Override
    public void remove(String name) {
        Hologram hologram = get(name);
        if (hologram == null)
            return;
        hologram.deleteHologram();
    }

    @Override
    public void setLines(String name, List<String> lines) {
        Hologram hologram = get(name);
        if (hologram == null)
            return;
        HologramData hologramData = hologram.getData();
        if (hologramData instanceof TextHologramData textHologramData) {
            textHologramData.setText(lines);
            hologram.refreshForViewers();
        }
    }

    @Override
    public @NotNull HologramDriverType getType() {
        return HologramDriverType.FANCY_HOLOGRAMS;
    }
}
