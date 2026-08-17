package io.github.anjoismysign.bloblib.message;

import io.github.anjoismysign.bloblib.translatable.BlobTranslatableSnippet;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ModernMessage extends AbstractMessage {
    private final String chat, hover, actionbar, title, subtitle;
    private final int fadeIn, stay, fadeOut;

    public ModernMessage(@NotNull String reference,
                         @Nullable String chat,
                         @Nullable String hover,
                         @Nullable String actionbar,
                         @Nullable String title,
                         @Nullable String subtitle,
                         int fadeIn,
                         int stay,
                         int fadeOut,
                         @Nullable BlobSound sound,
                         @NotNull String locale,
                         @Nullable ClickEvent clickEvent) {
        super(reference, sound, locale, clickEvent);
        this.chat = chat;
        this.hover = hover;
        this.actionbar = actionbar;
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @Override
    public void send(Player player) {
        if (chat != null && !chat.isEmpty()){
            if (hover == null) {
                player.sendMessage(BlobTranslatableSnippet.PARSE(chat, locale()));
            } else {
                TextComponent component = new TextComponent(TextComponent.fromLegacyText(chat));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));
                ClickEvent clickEvent = getClickEvent();
                if (clickEvent != null) {
                    component.setClickEvent(clickEvent);
                }
                player.spigot().sendMessage(component);
            }
        }
        if (actionbar != null && !actionbar.isEmpty()){
            player.sendActionBar(BlobTranslatableSnippet.PARSE(actionbar, locale()));
        }
        if (title != null && subtitle != null){
            player.sendTitle(BlobTranslatableSnippet.PARSE(title, locale()), BlobTranslatableSnippet.PARSE(subtitle, locale()), fadeIn, stay, fadeOut);
        }
    }

    @Override
    public void toCommandSender(CommandSender commandSender) {
        if (commandSender instanceof Player player)
            handle(player);
        else {
            if (chat != null && !chat.isEmpty()){
                commandSender.sendMessage(BlobTranslatableSnippet.PARSE(chat, locale()));
            }
            if (actionbar != null && !actionbar.isEmpty()){
                commandSender.sendMessage(BlobTranslatableSnippet.PARSE(actionbar, locale()));
            }
            if (title != null && subtitle != null && !title.isEmpty() && !subtitle.isEmpty()){
                commandSender.sendMessage(BlobTranslatableSnippet.PARSE(title, locale()));
                commandSender.sendMessage(BlobTranslatableSnippet.PARSE(subtitle, locale()));
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public @NotNull ModernMessage modify(Function<String, String> function) {
        return new ModernMessage(
                identifier(),
                chat == null ? null : function.apply(chat),
                hover == null ? null : function.apply(hover),
                actionbar == null ? null : function.apply(actionbar),
                title == null ? null : function.apply(title),
                subtitle == null ? null : function.apply(subtitle),
                fadeIn,
                stay,
                fadeOut,
                getSound(),
                locale(),
                getClickEvent()
        );
    }

    @Override
    public @NotNull ModernMessage onClick(ClickEvent event){
        return new ModernMessage(identifier(), chat, hover, actionbar, title, subtitle, fadeIn, stay, fadeOut, getSound(), locale(), event);
    }

    @Override
    public @NotNull ModernMessage toModernMessage() {
        return this;
    }

    public void write(@NotNull ConfigurationSection at){
        if (chat != null){
            at.set("Chat", chat);
            if (hover != null){
                at.set("Hover", hover);
            }
        }
        if (actionbar != null){
            at.set("Actionbar", actionbar);
        }
        if (title != null && subtitle != null){
            at.set("Title", title);
            at.set("Subtitle", subtitle);
            at.set("FadeIn", fadeIn);
            at.set("Stay", stay);
            at.set("FadeOut", fadeOut);
        }
    }

}
