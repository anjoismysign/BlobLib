package us.mytheria.bloblib.entities.translatable;

import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.entities.DataAssetType;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class BlobTranslatableBlock implements TranslatableBlock {
    @NotNull
    private final String locale, key;
    @NotNull
    private final List<String> block;

    public static BlobTranslatableBlock of(@NotNull String key,
                                           @NotNull String locale,
                                           @NotNull List<String> block) {
        Objects.requireNonNull(locale, "Locale cannot be null");
        Objects.requireNonNull(block, "Block cannot be null");
        return new BlobTranslatableBlock(key, locale, block);
    }

    private BlobTranslatableBlock(@NotNull String key,
                                  @NotNull String locale,
                                  @NotNull List<String> block) {
        this.key = key;
        this.locale = locale;
        this.block = block;
    }

    @NotNull
    public String getLocale() {
        return locale;
    }

    @NotNull
    public List<String> get() {
        return block;
    }

    @NotNull
    public String getReference() {
        return key;
    }

    public DataAssetType getType() {
        return DataAssetType.TRANSLATABLE_BLOCK;
    }

    @Override
    @NotNull
    public TranslatableBlock modify(Function<String, String> function) {
        return new BlobTranslatableBlock(key, locale, block.stream()
                .map(function)
                .toList());
    }
}
