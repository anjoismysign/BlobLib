package us.mytheria.bloblib.entities;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.api.BlobLibMessageAPI;
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
    public static SerialBlobMessage read(ConfigurationSection section) {
        return read(section, "en_us");
    }

    /**
     * Will read a BlobMessage from a ConfigurationSection
     *
     * @param section The section to read from
     * @return The BlobMessage
     */
    public static SerialBlobMessage read(ConfigurationSection section, @NotNull String locale) {
        String type = section.getString("Type");
        Optional<BlobSound> sound = section.contains("BlobSound") ?
                BlobSoundReader.parse(section) : Optional.empty();
        switch (type) {
            case "ACTIONBAR" -> {
                if (!section.contains("Message"))
                    throw new IllegalArgumentException("'Message' is required for ACTIONBAR messages at " + section.getCurrentPath());
                return new BlobActionbarMessage(TextColor.PARSE(section.getString("Message")),
                        sound.orElse(null),
                        locale);
            }
            case "TITLE" -> {
                if (!section.contains("Title"))
                    throw new IllegalArgumentException("'Title' is required for TITLE messages at " + section.getCurrentPath());
                if (!section.contains("Subtitle"))
                    throw new IllegalArgumentException("'Subtitle' is required for TITLE messages at " + section.getCurrentPath());
                int fadeIn = section.getInt("FadeIn", 10);
                int stay = section.getInt("Stay", 40);
                int fadeOut = section.getInt("FadeOut", 10);
                return new BlobTitleMessage(TextColor.PARSE(section.getString("Title")),
                        TextColor.PARSE(section.getString("Subtitle")),
                        fadeIn, stay, fadeOut, sound.orElse(null),
                        locale);
            }
            case "CHAT" -> {
                if (!section.contains("Message"))
                    throw new IllegalArgumentException("'Message' is required for CHAT messages at " + section.getCurrentPath());
                String hover = section.isString("Hover") ? TextColor.PARSE(section.getString("Hover")) : null;
                return new BlobChatMessage(TextColor.PARSE(section.getString("Message")),
                        hover,
                        sound.orElse(null),
                        locale, null);
            }
            case "ACTIONBAR_TITLE" -> {
                if (!section.contains("Title"))
                    throw new IllegalArgumentException("'Title' is required for ACTIONBAR_TITLE messages at " + section.getCurrentPath());
                if (!section.contains("Subtitle"))
                    throw new IllegalArgumentException("'Subtitle' is required for ACTIONBAR_TITLE messages at " + section.getCurrentPath());
                if (!section.contains("Actionbar"))
                    throw new IllegalArgumentException("'Actionbar' is required for ACTIONBAR_TITLE messages at " + section.getCurrentPath());
                int fadeIn = section.getInt("FadeIn", 10);
                int stay = section.getInt("Stay", 40);
                int fadeOut = section.getInt("FadeOut", 10);
                return new BlobActionbarTitleMessage(TextColor.PARSE(section.getString("Actionbar")),
                        TextColor.PARSE(section.getString("Title")),
                        TextColor.PARSE(section.getString("Subtitle")),
                        fadeIn, stay, fadeOut, sound.orElse(null),
                        locale);
            }
            case "CHAT_ACTIONBAR" -> {
                if (!section.contains("Chat"))
                    throw new IllegalArgumentException("'Chat' is required for CHAT_ACTIONBAR messages at " + section.getCurrentPath());
                if (!section.contains("Actionbar"))
                    throw new IllegalArgumentException("'Actionbar' is required for CHAT_ACTIONBAR messages at " + section.getCurrentPath());
                String hover = section.isString("Hover") ? TextColor.PARSE(section.getString("Hover")) : null;
                return new BlobChatActionbarMessage(TextColor.PARSE(section.getString("Chat")),
                        hover,
                        TextColor.PARSE(section.getString("Actionbar")),
                        sound.orElse(null),
                        locale, null);
            }
            case "CHAT_TITLE" -> {
                if (!section.contains("Chat"))
                    throw new IllegalArgumentException("'Chat' is required for CHAT_TITLE messages at " + section.getCurrentPath());
                if (!section.contains("Title"))
                    throw new IllegalArgumentException("'Title' is required for CHAT_TITLE messages at " + section.getCurrentPath());
                if (!section.contains("Subtitle"))
                    throw new IllegalArgumentException("'Subtitle' is required for CHAT_TITLE messages at " + section.getCurrentPath());
                String hover = section.isString("Hover") ? TextColor.PARSE(section.getString("Hover")) : null;
                int fadeIn = section.getInt("FadeIn", 10);
                int stay = section.getInt("Stay", 40);
                int fadeOut = section.getInt("FadeOut", 10);
                return new BlobChatTitleMessage(TextColor.PARSE(section.getString("Chat")),
                        hover,
                        TextColor.PARSE(section.getString("Title")),
                        TextColor.PARSE(section.getString("Subtitle")),
                        fadeIn, stay, fadeOut, sound.orElse(null),
                        locale, null);
            }
            case "CHAT_ACTIONBAR_TITLE" -> {
                if (!section.contains("Chat"))
                    throw new IllegalArgumentException("'Chat' is required for CHAT_ACTIONBAR_TITLE messages at " + section.getCurrentPath());
                if (!section.contains("Actionbar"))
                    throw new IllegalArgumentException("'Actionbar' is required for CHAT_ACTIONBAR_TITLE messages at " + section.getCurrentPath());
                if (!section.contains("Title"))
                    throw new IllegalArgumentException("'Title' is required for CHAT_ACTIONBAR_TITLE messages at " + section.getCurrentPath());
                if (!section.contains("Subtitle"))
                    throw new IllegalArgumentException("'Subtitle' is required for CHAT_ACTIONBAR_TITLE messages at " + section.getCurrentPath());
                String hover = section.isString("Hover") ? TextColor.PARSE(section.getString("Hover")) : null;
                int fadeIn = section.getInt("FadeIn", 10);
                int stay = section.getInt("Stay", 40);
                int fadeOut = section.getInt("FadeOut", 10);
                return new BlobChatActionbarTitleMessage(TextColor.PARSE(section.getString("Chat")),
                        hover,
                        TextColor.PARSE(section.getString("Actionbar")),
                        TextColor.PARSE(section.getString("Title")),
                        TextColor.PARSE(section.getString("Subtitle")),
                        fadeIn, stay, fadeOut, sound.orElse(null),
                        locale, null);
            }
            default ->
                    throw new IllegalArgumentException("Invalid message type: '" + type + "' at " + section.getCurrentPath());
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
            return Optional.ofNullable(BlobLibMessageAPI.getInstance().getMessage(parentConfigurationSection.getString("BlobMessage")));
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
        return Optional.ofNullable(BlobLibMessageAPI.getInstance().getMessage(section.getString("BlobMessage")));
    }
}
