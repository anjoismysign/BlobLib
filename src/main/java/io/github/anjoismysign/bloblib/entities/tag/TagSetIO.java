package io.github.anjoismysign.bloblib.entities.tag;

import io.github.anjoismysign.bloblib.api.BlobLibTagAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TagSetIO {

    public static void WRITE(@NotNull ConfigurationSection section,
                             @NotNull TagSet tagSet) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(tagSet);
        section.set("Inclusions", tagSet.getInclusions());
    }

    @NotNull
    public static TagSet READ(@NotNull ConfigurationSection section,
                              @NotNull String key) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(key);
        List<String> readInclusions = section.getStringList("Inclusions");
        List<String> readExclusions = section.getStringList("Exclusions");
        List<String> readIncludeSet = section.getStringList("Include-Tags");
        List<String> readExcludeSet = section.getStringList("Exclude-Tags");
        Set<String> inclusions = new HashSet<>();
        inclusions.addAll(readInclusions);
        readIncludeSet.forEach(reference -> {
            TagSet tagSet = BlobLibTagAPI.getInstance().getTagSet(reference);
            if (tagSet == null)
                return;
            inclusions.addAll(tagSet.getInclusions());
        });
        Set<String> exclusions = new HashSet<>();
        exclusions.addAll(readExclusions);
        readExcludeSet.forEach(reference -> {
            TagSet tagSet = BlobLibTagAPI.getInstance().getTagSet(reference);
            if (tagSet == null)
                return;
            exclusions.addAll(tagSet.getInclusions());
        });
        return new TagSet(inclusions.stream()
                .filter(s -> !exclusions.contains(s))
                .collect(Collectors.toSet()), key);
    }
}
