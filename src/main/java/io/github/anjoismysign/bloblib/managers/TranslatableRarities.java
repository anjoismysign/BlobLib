package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.entities.TranslatableRarity;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableItem;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record TranslatableRarities(@NotNull MinecraftRarities minecraft,
                                   @NotNull Set<CustomRarity> custom) {

    @Nullable
    public TranslatableRarity getRarity(@NotNull String rarityName){
        Objects.requireNonNull(rarityName, "'rarityName' cannot be null");
        switch (rarityName.toLowerCase(Locale.ROOT)){
            case "common"->{
                return minecraft.common;
            }
            case "uncommon"->{
                return minecraft.uncommon;
            }
            case "rare"->{
                return minecraft.rare;
            }
            case "epic"->{
                return minecraft.epic;
            }
            default -> {
                return custom.stream().filter(rarity->rarity.identifier.equals(rarityName)).findFirst().orElse(null);
            }
        }
    }

    @NotNull
    public TranslatableRarity getRarity(@NotNull ItemStack itemStack){
        Objects.requireNonNull(itemStack, "'itemStack' cannot be null");
        Material material = itemStack.getType();
        if (material.isAir()){
            throw new IllegalArgumentException("'itemStack' cannot be air!");
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta.hasRarity()){
            ItemRarity rarity = meta.getRarity();
            return minecraft.minecraft(rarity);
        }
        @Nullable TranslatableItem item = TranslatableItem.byItemStack(itemStack);
        if (item == null){
            ItemRarity rarity = Objects.requireNonNull(material.asItemType().getItemRarity(), "'"+material.name()+"' has no rarity!");
            return minecraft.minecraft(rarity);
        }
        return item.getRarity();
    }

    /**
     * Reads BlobLibRarities from a YamlConfiguration.
     * Fails fast.
     * @param yamlConfiguration The configuration to read from
     * @return The BlobLibRarities
     */
    @NotNull
    public static TranslatableRarities of(@NotNull YamlConfiguration yamlConfiguration) {
        @Nullable ConfigurationSection minecraftSection = yamlConfiguration.getConfigurationSection("minecraft");
        if (minecraftSection == null) {
            throw new IllegalArgumentException("Missing 'minecraft' section");
        }

        CustomRarity common = parseRarity(minecraftSection, "common");
        CustomRarity uncommon = parseRarity(minecraftSection, "uncommon");
        CustomRarity rare = parseRarity(minecraftSection, "rare");
        CustomRarity epic = parseRarity(minecraftSection, "epic");

        MinecraftRarities defaultRarities = new MinecraftRarities(common, uncommon, rare, epic);

        Set<CustomRarity> customRarities = new HashSet<>();
        @Nullable ConfigurationSection customSection = yamlConfiguration.getConfigurationSection("custom");
        if (customSection != null) {
            for (String key : customSection.getKeys(false)) {
                customRarities.add(parseRarity(customSection, key));
            }
        }

        return new TranslatableRarities(defaultRarities, customRarities);
    }

    private static @NotNull CustomRarity parseRarity(@NotNull ConfigurationSection section,
                                                                     @NotNull String rarityKey) {
        ConfigurationSection raritySection = section.getConfigurationSection(rarityKey);
        if (raritySection == null) {
            throw new IllegalArgumentException("Missing section for rarity: " + rarityKey);
        }

        String color = raritySection.getString("color");
        if (color == null) {
            throw new IllegalArgumentException("Missing color for rarity: " + rarityKey);
        }

        ConfigurationSection descriptionsSection = raritySection.getConfigurationSection("descriptions");
        Map<String, String> descriptions = new HashMap<>();
        if (descriptionsSection != null) {
            for (String lang : descriptionsSection.getKeys(false)) {
                String description = descriptionsSection.getString(lang);
                if (description != null) {
                    descriptions.put(lang, description);
                }
            }
        }

        return new CustomRarity(rarityKey, color, descriptions);
    }

    public record MinecraftRarities(@NotNull CustomRarity common,
                                    @NotNull CustomRarity uncommon,
                                    @NotNull CustomRarity rare,
                                    @NotNull CustomRarity epic){
        public TranslatableRarity minecraft(ItemRarity rarity){
            switch (rarity){
                case COMMON -> {
                    return common;
                }
                case UNCOMMON -> {
                    return uncommon;
                }
                case RARE -> {
                    return rare;
                }
                case EPIC -> {
                    return epic;
                }
                default -> {
                    throw new IllegalArgumentException("'"+rarity.name()+"' is not implemented yet");
                }
            }
        }
    }

    public record CustomRarity(
            @NotNull String identifier,
            @NotNull String color,
            @NotNull Map<String, String> descriptions) implements TranslatableRarity {
        @Override
        public @NotNull String getIdentifier() {
            return identifier;
        }

        @Override
        public @NotNull TextColor getTextColor() {
            return Objects.requireNonNull(TextColor.fromHexString(color), "Invalid HEX RGB: "+color);
        }
    }

}
