package us.mytheria.bloblib.entities;

import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.message.*;
import us.mytheria.bloblib.utilities.TextColor;

import java.util.Optional;

/**
 * @author anjoismysign
 * This clas will help with parsing of BlobMessage's
 * <p>
 * Recommended method is parse(ConfigurationSection section)
 */
public class BlobMessageReader {
    /**
     * Will read a BlobMessage from a ConfigurationSection
     *
     * @param section The section to read from
     * @return The BlobMessage
     */
    public static SerialBlobMessage read(ConfigurationSection section) {
        String type = section.getString("Type");
        Optional<BlobSound> sound = section.contains("BlobSound") ?
                BlobSoundReader.parse(section) : Optional.empty();
        switch (type) {
            case "ACTIONBAR" -> {
                if (!section.contains("Message"))
                    throw new IllegalArgumentException("'Message' is required for ACTIONBAR messages.");
                return new BlobActionbarMessage(TextColor.PARSE(section.getString("Message")), sound.orElse(null));
            }
            case "TITLE" -> {
                if (!section.contains("Title"))
                    throw new IllegalArgumentException("'Title' is required for TITLE messages.");
                if (!section.contains("Subtitle"))
                    throw new IllegalArgumentException("'Subtitle' is required for TITLE messages.");
                int fadeIn = section.getInt("FadeIn", 10);
                int stay = section.getInt("Stay", 40);
                int fadeOut = section.getInt("FadeOut", 10);
                return new BlobTitleMessage(TextColor.PARSE(section.getString("Title")),
                        TextColor.PARSE(section.getString("Subtitle")),
                        fadeIn, stay, fadeOut, sound.orElse(null));
            }
            case "CHAT" -> {
                if (!section.contains("Message"))
                    throw new IllegalArgumentException("'Message' is required for CHAT messages.");
                return new BlobChatMessage(TextColor.PARSE(section.getString("Message")), sound.orElse(null));
            }
            case "ACTIONBAR_TITLE" -> {
                if (!section.contains("Title"))
                    throw new IllegalArgumentException("'Title' is required for ACTIONBAR_TITLE messages.");
                if (!section.contains("Subtitle"))
                    throw new IllegalArgumentException("'Subtitle' is required for ACTIONBAR_TITLE messages.");
                if (!section.contains("Actionbar"))
                    throw new IllegalArgumentException("'Actionbar' is required for ACTIONBAR_TITLE messages.");
                int fadeIn = section.getInt("FadeIn", 10);
                int stay = section.getInt("Stay", 40);
                int fadeOut = section.getInt("FadeOut", 10);
                return new BlobActionbarTitleMessage(TextColor.PARSE(section.getString("Title")),
                        TextColor.PARSE(section.getString("Subtitle")),
                        TextColor.PARSE(section.getString("Actionbar")),
                        fadeIn, stay, fadeOut, sound.orElse(null));
            }
            case "CHAT_ACTIONBAR" -> {
                if (!section.contains("Chat"))
                    throw new IllegalArgumentException("'Chat' is required for CHAT_ACTIONBAR messages.");
                if (!section.contains("Actionbar"))
                    throw new IllegalArgumentException("'Actionbar' is required for CHAT_ACTIONBAR messages.");
                return new BlobChatActionbarMessage(TextColor.PARSE(section.getString("Chat")),
                        TextColor.PARSE(section.getString("Actionbar")),
                        sound.orElse(null));
            }
            case "CHAT_TITLE" -> {
                if (!section.contains("Chat"))
                    throw new IllegalArgumentException("'Chat' is required for CHAT_TITLE messages.");
                if (!section.contains("Title"))
                    throw new IllegalArgumentException("'Title' is required for CHAT_TITLE messages.");
                if (!section.contains("Subtitle"))
                    throw new IllegalArgumentException("'Subtitle' is required for CHAT_TITLE messages.");
                int fadeIn = section.getInt("FadeIn", 10);
                int stay = section.getInt("Stay", 40);
                int fadeOut = section.getInt("FadeOut", 10);
                return new BlobChatTitleMessage(TextColor.PARSE(section.getString("Chat")),
                        TextColor.PARSE(section.getString("Title")),
                        TextColor.PARSE(section.getString("Subtitle")),
                        fadeIn, stay, fadeOut, sound.orElse(null));
            }
            case "CHAT_ACTIONBAR_TITLE" -> {
                if (!section.contains("Chat"))
                    throw new IllegalArgumentException("'Chat' is required for CHAT_ACTIONBAR_TITLE messages.");
                if (!section.contains("Actionbar"))
                    throw new IllegalArgumentException("'Actionbar' is required for CHAT_ACTIONBAR_TITLE messages.");
                if (!section.contains("Title"))
                    throw new IllegalArgumentException("'Title' is required for CHAT_ACTIONBAR_TITLE messages.");
                if (!section.contains("Subtitle"))
                    throw new IllegalArgumentException("'Subtitle' is required for CHAT_ACTIONBAR_TITLE messages.");
                int fadeIn = section.getInt("FadeIn", 10);
                int stay = section.getInt("Stay", 40);
                int fadeOut = section.getInt("FadeOut", 10);
                return new BlobChatActionbarTitleMessage(TextColor.PARSE(section.getString("Chat")),
                        TextColor.PARSE(section.getString("Actionbar")),
                        TextColor.PARSE(section.getString("Title")),
                        TextColor.PARSE(section.getString("Subtitle")),
                        fadeIn, stay, fadeOut, sound.orElse(null));
            }
            default -> throw new IllegalArgumentException("Invalid message type: " + type);
        }
    }

    /**
     * Whenever detecting BlobMessage being a single line String, will attempt to read it
     * as a ReferenceBlobMessage.
     * Otherwise, will attempt to read it as a SerialBlobMessage
     *
     * @param parentConfigurationSection The parent configuration section
     * @return BlobMessage
     */
    public static Optional<BlobMessage> parse(ConfigurationSection parentConfigurationSection) {
        if (!parentConfigurationSection.contains("BlobMessage"))
            return Optional.empty();
        if (parentConfigurationSection.isString("BlobMessage"))
            return Optional.ofNullable(BlobLibAssetAPI.getMessage(parentConfigurationSection.getString("BlobMessage")));
        return Optional.of(read(parentConfigurationSection.getConfigurationSection("BlobMessage")));
    }

    /**
     * Reads a ReferenceBlobMessage from a ConfigurationSection
     *
     * @param section The ConfigurationSection
     * @return ReferenceBlobMessage
     */
    public static Optional<ReferenceBlobMessage> readReference(ConfigurationSection section) {
        if (!section.contains("BlobMessage"))
            return Optional.empty();
        if (!section.isString("BlobMessage"))
            throw new IllegalArgumentException("'BlobMessage' must be a String");
        return Optional.ofNullable(BlobLibAssetAPI.getMessage(section.getString("BlobMessage")));
    }
}
