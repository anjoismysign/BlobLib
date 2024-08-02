package us.mytheria.bloblib.entities;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.api.BlobLibSoundAPI;
import us.mytheria.bloblib.entities.message.BlobSound;
import us.mytheria.bloblib.entities.message.MessageAudience;
import us.mytheria.bloblib.exception.ConfigurationFieldException;

import java.util.Objects;
import java.util.Optional;

public class BlobSoundReader {
    public static BlobSound read(@NotNull ConfigurationSection section,
                                 @NotNull String key) {
        if (!section.contains("Sound"))
            throw new ConfigurationFieldException("'Sound' is not defined");
        if (!section.contains("Volume"))
            throw new ConfigurationFieldException("'Volume' is not defined");
        if (!section.contains("Pitch"))
            throw new ConfigurationFieldException("'Pitch' is not defined");
        Optional<SoundCategory> category = Optional.empty();
        if (section.contains("Category"))
            try {
                category = Optional.of(SoundCategory.valueOf(section.getString("Category")));
            } catch (IllegalArgumentException e) {
                throw new ConfigurationFieldException("Invalid Sound's Category: " + section.getString("Category"));
            }
        MessageAudience audience = MessageAudience.PLAYER;
        if (section.contains("Audience"))
            try {
                audience = MessageAudience.valueOf(section.getString("Audience"));
            } catch (IllegalArgumentException e) {
                throw new ConfigurationFieldException("Invalid Sound's Audience: " + section.getString("Audience"));
            }
        return new BlobSound(
                Sound.valueOf(section.getString("Sound")),
                (float) section.getDouble("Volume"),
                (float) section.getDouble("Pitch"),
                category.orElse(null),
                audience,
                key
        );
    }

    public static Optional<BlobSound> parse(@NotNull ConfigurationSection parent,
                                            @Nullable String key) {
        if (!parent.contains("BlobSound"))
            return Optional.empty();
        if (parent.isString("BlobSound")) {
            String sound = parent.getString("BlobSound");
            String[] split = sound.split(":");
            if (split.length == 1)
                return Optional.ofNullable(BlobLibSoundAPI.getInstance().getSound(sound));
            else if (split.length == 2) {
                String reference = split[0];
                Optional<BlobSound> optional = Optional.ofNullable(BlobLibSoundAPI.getInstance().getSound(reference));
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
                        blobSound.pitch(), blobSound.soundCategory(), audience, reference));
            }
        }
        Objects.requireNonNull(key, "Key must be provided when parsing a BlobSound");
        return Optional.of(read(parent.getConfigurationSection("BlobSound"), key));
    }
}
