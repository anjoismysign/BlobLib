package io.github.anjoismysign.bloblib.entities;

import io.github.anjoismysign.bloblib.managers.BlobLibConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public interface TranslatableRarity {

    static TranslatableRarity of(@NotNull ItemStack itemStack){
        var rarities = BlobLibConfigManager.getInstance().getRarities();
        return rarities.getRarity(itemStack);
    }

    @NotNull
    String getIdentifier();

    @NotNull
    TextColor getTextColor();

    default Color toColor(int alpha){
        int red = getTextColor().red();
        int green = getTextColor().green();
        int blue = getTextColor().blue();
        return Color.fromARGB(alpha, red, green, blue);
    }

    @NotNull
    Map<String, String> descriptions();

    @Nullable
    default Component descriptionFor(@NotNull String locale){
        Map<String, String> descriptions = descriptions();
        BlobLibConfigManager manager = BlobLibConfigManager.getInstance();
        @Nullable String raw = Optional.of(manager.getRealLocale(locale)).orElse(descriptions.get("en_us"));
        if (raw == null){
            return null;
        }
        return MiniMessage.miniMessage().deserialize(raw);
    }

}
