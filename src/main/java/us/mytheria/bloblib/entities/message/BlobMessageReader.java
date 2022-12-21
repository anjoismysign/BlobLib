package us.mytheria.bloblib.entities.message;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class BlobMessageReader {
    public static BlobMessage read(ConfigurationSection section) {
        String type = section.getString("Type");
        switch (type) {
            case "ACTIONBAR" -> {
                if (!section.contains("Message"))
                    throw new IllegalArgumentException("'Message' is required for ACTIONBAR messages.");
                return new BlobActionBar(ChatColor.translateAlternateColorCodes('&', section.getString("Message")));
            }
            case "TITLE" -> {
                if (!section.contains("Title"))
                    throw new IllegalArgumentException("'Title' is required for TITLE messages.");
                if (!section.contains("Subtitle"))
                    throw new IllegalArgumentException("'Subtitle' is required for TITLE messages.");
                int fadeIn = section.getInt("FadeIn", 10);
                int stay = section.getInt("Stay", 40);
                int fadeOut = section.getInt("FadeOut", 10);
                return new BlobTitle(ChatColor.translateAlternateColorCodes('&', section.getString("Title")),
                        ChatColor.translateAlternateColorCodes('&', section.getString("Subtitle")),
                        fadeIn, stay, fadeOut);
            }
            case "CHAT" -> {
                if (!section.contains("Message"))
                    throw new IllegalArgumentException("'Message' is required for CHAT messages.");
                return new BlobChatMessage(section.getString("Message"));
            }
            default -> throw new IllegalArgumentException("Invalid message type: " + type);
        }
    }
}
