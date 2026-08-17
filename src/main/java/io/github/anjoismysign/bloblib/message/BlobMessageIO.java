package io.github.anjoismysign.bloblib.message;

import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import io.github.anjoismysign.bloblib.utility.TextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * @author anjoismysign
 * This clas will help with parsing of BlobMessage's
 * <p>
 * Recommended method is parse(ConfigurationSection section)
 */
public enum BlobMessageIO {
    INSTANCE;

    public void write(@NotNull ConfigurationSection at,
                      @NotNull BlobMessage message){
        ModernMessage modernMessage = message.toModernMessage();
        modernMessage.write(at);
    }

    /**
     * Will read a BlobMessage from a ConfigurationSection
     *
     * @param section The section to read from
     * @return The BlobMessage
     */
    public BlobMessage read(@NotNull ConfigurationSection section,
                            @NotNull String locale,
                            @NotNull String key) {
        Optional<BlobSound> sound = section.contains("BlobSound") ?
                BlobSoundReader.parse(section, null) : Optional.empty();
        String type = section.getString("Type");
        if (type == null) {
            @Nullable String chat = section.getString("Chat");
            chat = chat == null ? null : TextColor.PARSE(chat);
            @Nullable String hover = section.getString("Hover");
            hover = hover == null ? null : TextColor.PARSE(hover);
            @Nullable String actionbar = section.getString("Actionbar");
            actionbar = actionbar == null ? null : TextColor.PARSE(actionbar);
            @Nullable String title = section.getString("Title");
            title = title == null ? null : TextColor.PARSE(title);
            @Nullable String subtitle = section.getString("Subtitle");
            subtitle = subtitle == null ? null : TextColor.PARSE(subtitle);
            if (chat == null && actionbar == null && (title == null || subtitle == null)){
                throw new ConfigurationFieldException("There's neither 'Chat', 'Actionbar' or 'Title' and 'Subtitle' at " + section.getCurrentPath());
            }
            int fadeIn = section.getInt("FadeIn", 10);
            int stay = section.getInt("Stay", 70);
            int fadeOut = section.getInt("FadeOut", 20);
            return new ModernMessage(key, chat, hover, actionbar, title, subtitle, fadeIn, stay, fadeOut, sound.orElse(null), locale, null);
        }
        switch (type) {
            case "ACTIONBAR" -> {
                if (!section.contains("Message"))
                    throw new ConfigurationFieldException("'Message' is required for ACTIONBAR messages at " + section.getCurrentPath());
                return new BlobActionbarMessage(key, TextColor.PARSE(section.getString("Message")),
                        sound.orElse(null),
                        locale);
            }
            case "TITLE" -> {
                if (!section.contains("Title"))
                    throw new ConfigurationFieldException("'Title' is required for TITLE messages at " + section.getCurrentPath());
                if (!section.contains("Subtitle"))
                    throw new ConfigurationFieldException("'Subtitle' is required for TITLE messages at " + section.getCurrentPath());
                int fadeIn = section.getInt("FadeIn", 10);
                int stay = section.getInt("Stay", 40);
                int fadeOut = section.getInt("FadeOut", 10);
                return new BlobTitleMessage(key, TextColor.PARSE(section.getString("Title")),
                        TextColor.PARSE(section.getString("Subtitle")),
                        fadeIn, stay, fadeOut, sound.orElse(null),
                        locale);
            }
            case "CHAT" -> {
                if (!section.contains("Message"))
                    throw new ConfigurationFieldException("'Message' is required for CHAT messages at " + section.getCurrentPath());
                String hover = section.isString("Hover") ? TextColor.PARSE(section.getString("Hover")) : null;
                return new BlobChatMessage(key, TextColor.PARSE(section.getString("Message")),
                        hover,
                        sound.orElse(null),
                        locale, null);
            }
            case "ACTIONBAR_TITLE" -> {
                if (!section.contains("Title"))
                    throw new ConfigurationFieldException("'Title' is required for ACTIONBAR_TITLE messages at " + section.getCurrentPath());
                if (!section.contains("Subtitle"))
                    throw new ConfigurationFieldException("'Subtitle' is required for ACTIONBAR_TITLE messages at " + section.getCurrentPath());
                if (!section.contains("Actionbar"))
                    throw new ConfigurationFieldException("'Actionbar' is required for ACTIONBAR_TITLE messages at " + section.getCurrentPath());
                int fadeIn = section.getInt("FadeIn", 10);
                int stay = section.getInt("Stay", 40);
                int fadeOut = section.getInt("FadeOut", 10);
                return new BlobActionbarTitleMessage(key, TextColor.PARSE(section.getString("Actionbar")),
                        TextColor.PARSE(section.getString("Title")),
                        TextColor.PARSE(section.getString("Subtitle")),
                        fadeIn, stay, fadeOut, sound.orElse(null),
                        locale);
            }
            case "CHAT_ACTIONBAR" -> {
                if (!section.contains("Chat"))
                    throw new ConfigurationFieldException("'Chat' is required for CHAT_ACTIONBAR messages at " + section.getCurrentPath());
                if (!section.contains("Actionbar"))
                    throw new ConfigurationFieldException("'Actionbar' is required for CHAT_ACTIONBAR messages at " + section.getCurrentPath());
                String hover = section.isString("Hover") ? TextColor.PARSE(section.getString("Hover")) : null;
                return new BlobChatActionbarMessage(key, TextColor.PARSE(section.getString("Chat")),
                        hover,
                        TextColor.PARSE(section.getString("Actionbar")),
                        sound.orElse(null),
                        locale, null);
            }
            case "CHAT_TITLE" -> {
                if (!section.contains("Chat"))
                    throw new ConfigurationFieldException("'Chat' is required for CHAT_TITLE messages at " + section.getCurrentPath());
                if (!section.contains("Title"))
                    throw new ConfigurationFieldException("'Title' is required for CHAT_TITLE messages at " + section.getCurrentPath());
                if (!section.contains("Subtitle"))
                    throw new ConfigurationFieldException("'Subtitle' is required for CHAT_TITLE messages at " + section.getCurrentPath());
                String hover = section.isString("Hover") ? TextColor.PARSE(section.getString("Hover")) : null;
                int fadeIn = section.getInt("FadeIn", 10);
                int stay = section.getInt("Stay", 40);
                int fadeOut = section.getInt("FadeOut", 10);
                return new BlobChatTitleMessage(key, TextColor.PARSE(section.getString("Chat")),
                        hover,
                        TextColor.PARSE(section.getString("Title")),
                        TextColor.PARSE(section.getString("Subtitle")),
                        fadeIn, stay, fadeOut, sound.orElse(null),
                        locale, null);
            }
            case "CHAT_ACTIONBAR_TITLE" -> {
                if (!section.contains("Chat"))
                    throw new ConfigurationFieldException("'Chat' is required for CHAT_ACTIONBAR_TITLE messages at " + section.getCurrentPath());
                if (!section.contains("Actionbar"))
                    throw new ConfigurationFieldException("'Actionbar' is required for CHAT_ACTIONBAR_TITLE messages at " + section.getCurrentPath());
                if (!section.contains("Title"))
                    throw new ConfigurationFieldException("'Title' is required for CHAT_ACTIONBAR_TITLE messages at " + section.getCurrentPath());
                if (!section.contains("Subtitle"))
                    throw new ConfigurationFieldException("'Subtitle' is required for CHAT_ACTIONBAR_TITLE messages at " + section.getCurrentPath());
                String hover = section.isString("Hover") ? TextColor.PARSE(section.getString("Hover")) : null;
                int fadeIn = section.getInt("FadeIn", 10);
                int stay = section.getInt("Stay", 40);
                int fadeOut = section.getInt("FadeOut", 10);
                return new BlobChatActionbarTitleMessage(key, TextColor.PARSE(section.getString("Chat")),
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
}
