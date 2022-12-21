package us.mytheria.bloblib.entities.message;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

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
        return new BlobSound(
                Sound.valueOf(section.getString("Sound")),
                (float) section.getDouble("Volume"),
                (float) section.getDouble("Pitch")
        );
    }

    public static BlobSound read(ConfigurationSection section) {
        return read(section, null);
    }
}
