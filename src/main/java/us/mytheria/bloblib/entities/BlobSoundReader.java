package us.mytheria.bloblib.entities;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.message.BlobSound;

import java.util.Optional;

public class BlobSoundReader {
    public static BlobSound read(ConfigurationSection section) {
        if (!section.contains("Sound"))
            throw new IllegalArgumentException("'Sound' is not defined");
        if (!section.contains("Volume"))
            throw new IllegalArgumentException("'Volume' is not defined");
        if (!section.contains("Pitch"))
            throw new IllegalArgumentException("'Pitch' is not defined");
        Optional<SoundCategory> category = Optional.empty();
        if (section.contains("Category"))
            try {
                category = Optional.of(SoundCategory.valueOf(section.getString("Category")));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid Sound's Category: " + section.getString("Category"));
            }
        return new BlobSound(
                Sound.valueOf(section.getString("Sound")),
                (float) section.getDouble("Volume"),
                (float) section.getDouble("Pitch"),
                category.orElse(null)
        );
    }

    public static Optional<BlobSound> parse(ConfigurationSection parentConfigurationSection) {
        if (!parentConfigurationSection.contains("BlobSound"))
            return Optional.empty();
        if (parentConfigurationSection.isString("BlobSound"))
            return Optional.ofNullable(BlobLibAssetAPI.getSound(parentConfigurationSection.getString("BlobSound")));
        return Optional.of(read(parentConfigurationSection.getConfigurationSection("BlobSound")));
    }
}
