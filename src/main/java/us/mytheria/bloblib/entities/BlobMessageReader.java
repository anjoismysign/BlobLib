package us.mytheria.bloblib.entities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.BlobLibAPI;
import us.mytheria.bloblib.entities.message.*;

import java.util.Optional;

public class BlobMessageReader {
    public static SerialBlobMessage read(ConfigurationSection section) {
        String type = section.getString("Type");
        Optional<BlobSound> sound = section.contains("BlobSound") ?
                BlobSoundReader.parse(section) : Optional.empty();
        switch (type) {
            case "ACTIONBAR" -> {
                if (!section.contains("Message"))
                    throw new IllegalArgumentException("'Message' is required for ACTIONBAR messages.");
                return new BlobActionbarMessage(ChatColor
                        .translateAlternateColorCodes('&', section
                                .getString("Message")), sound.orElse(null));
            }
            case "TITLE" -> {
                if (!section.contains("Title"))
                    throw new IllegalArgumentException("'Title' is required for TITLE messages.");
                if (!section.contains("Subtitle"))
                    throw new IllegalArgumentException("'Subtitle' is required for TITLE messages.");
                int fadeIn = section.getInt("FadeIn", 10);
                int stay = section.getInt("Stay", 40);
                int fadeOut = section.getInt("FadeOut", 10);
                return new BlobTitleMessage(ChatColor.translateAlternateColorCodes('&', section.getString("Title")),
                        ChatColor.translateAlternateColorCodes('&', section.getString("Subtitle")),
                        fadeIn, stay, fadeOut, sound.orElse(null));
            }
            case "CHAT" -> {
                if (!section.contains("Message"))
                    throw new IllegalArgumentException("'Message' is required for CHAT messages.");
                return new BlobChatMessage(section.getString("Message"),
                        sound.orElse(null));
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
                return new BlobActionbarTitleMessage(ChatColor.translateAlternateColorCodes('&', section.getString("Title")),
                        ChatColor.translateAlternateColorCodes('&', section.getString("Subtitle")),
                        ChatColor.translateAlternateColorCodes('&', section.getString("Actionbar")),
                        fadeIn, stay, fadeOut, sound.orElse(null));
            }
            case "CHAT_ACTIONBAR" -> {
                if (!section.contains("Chat"))
                    throw new IllegalArgumentException("'Chat' is required for CHAT_ACTIONBAR messages.");
                if (!section.contains("Actionbar"))
                    throw new IllegalArgumentException("'Actionbar' is required for CHAT_ACTIONBAR messages.");
                return new BlobChatActionbarMessage(ChatColor.translateAlternateColorCodes('&', section.getString("Chat")),
                        ChatColor.translateAlternateColorCodes('&', section.getString("Actionbar")),
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
                return new BlobChatTitleMessage(ChatColor.translateAlternateColorCodes('&', section.getString("Chat")),
                        ChatColor.translateAlternateColorCodes('&', section.getString("Title")),
                        ChatColor.translateAlternateColorCodes('&', section.getString("Subtitle")),
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
                return new BlobChatActionbarTitleMessage(ChatColor.translateAlternateColorCodes('&', section.getString("Chat")),
                        ChatColor.translateAlternateColorCodes('&', section.getString("Actionbar")),
                        ChatColor.translateAlternateColorCodes('&', section.getString("Title")),
                        ChatColor.translateAlternateColorCodes('&', section.getString("Subtitle")),
                        fadeIn, stay, fadeOut, sound.orElse(null));
            }
            default -> throw new IllegalArgumentException("Invalid message type: " + type);
        }
    }

    public static Optional<SerialBlobMessage> parse(ConfigurationSection parentConfigurationSection) {
        if (!parentConfigurationSection.contains("BlobMessage"))
            return Optional.empty();
        if (parentConfigurationSection.isString("BlobMessage"))
            return Optional.ofNullable(BlobLibAPI.getMessage(parentConfigurationSection.getString("BlobMessage")));
        return Optional.of(read(parentConfigurationSection.getConfigurationSection("BlobMessage")));
    }
}
