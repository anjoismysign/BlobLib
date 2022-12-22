package us.mytheria.bloblib.entities;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.entities.message.BlobSound;

import java.util.Optional;
import java.util.function.Function;

public class BlobSoundReader {
    public static BlobSound read(ConfigurationSection section,
                                 Function<String, BlobSound> managerFunction) {
        if (managerFunction != null) {
            if (section.contains("BlobSound")) {
                String blobSound = section.getString("BlobSound");
                BlobSound sound = managerFunction.apply(blobSound);
                if (sound == null)
                    throw new IllegalArgumentException("Invalid BlobSound: " + blobSound);
                return sound;
            }
        }
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

    public static BlobSound read(ConfigurationSection section) {
        return read(section, null);
    }
}
