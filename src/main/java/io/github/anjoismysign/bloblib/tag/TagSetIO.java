package io.github.anjoismysign.bloblib.tag;

import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
        if (!readExclusions.isEmpty()){
            throw new ConfigurationFieldException("'Exclusions' is deprecated and won't be used");
        }
        List<String> readIncludeSet = section.getStringList("Include-Tags");
        if (!readExclusions.isEmpty()){
            throw new ConfigurationFieldException("'Include-Tags' is deprecated and won't be used");
        }
        List<String> readExcludeSet = section.getStringList("Exclude-Tags");
        if (!readExclusions.isEmpty()){
            throw new ConfigurationFieldException("'Exclude-Tags' is deprecated and won't be used");
        }
        Set<String> inclusions = new HashSet<>(readInclusions);
        return new TagSet(inclusions, key);
    }
}
