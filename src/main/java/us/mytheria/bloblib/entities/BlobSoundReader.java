package us.mytheria.bloblib.entities;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.message.BlobSound;
import us.mytheria.bloblib.entities.message.MessageAudience;

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
        MessageAudience audience = MessageAudience.PLAYER;
        if (section.contains("Audience"))
            try {
                audience = MessageAudience.valueOf(section.getString("Audience"));
            } catch (IllegalArgumentException e) {
                BlobLib.getAnjoLogger().singleError("Invalid Sound's Audience: " + section.getString("Audience"));
            }
        return new BlobSound(
                Sound.valueOf(section.getString("Sound")),
                (float) section.getDouble("Volume"),
                (float) section.getDouble("Pitch"),
                category.orElse(null),
                audience
        );
    }

    public static Optional<BlobSound> parse(ConfigurationSection parentConfigurationSection) {
        if (!parentConfigurationSection.contains("BlobSound"))
            return Optional.empty();
        if (parentConfigurationSection.isString("BlobSound")) {
            String sound = parentConfigurationSection.getString("BlobSound");
            String[] split = sound.split(":");
            if (split.length == 1)
                return Optional.ofNullable(BlobLibAssetAPI.getSound(sound));
            else if (split.length == 2) {
                Optional<BlobSound> optional = Optional.ofNullable(BlobLibAssetAPI.getSound(split[0]));
                if (optional.isEmpty())
                    return Optional.empty();
                MessageAudience audience;
                try {
                    audience = MessageAudience.valueOf(split[1]);
                } catch (IllegalArgumentException e) {
                    BlobLib.getAnjoLogger().singleError("Invalid Sound's Audience: " + split[1]);
                    return Optional.empty();
                }
                BlobSound blobSound = optional.get();
                return Optional.of(new BlobSound(blobSound.sound(), blobSound.volume(),
                        blobSound.pitch(), blobSound.soundCategory(), audience));
            }
        }
        return Optional.of(read(parentConfigurationSection.getConfigurationSection("BlobSound")));
    }
}
