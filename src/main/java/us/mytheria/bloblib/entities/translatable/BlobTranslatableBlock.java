package us.mytheria.bloblib.entities.translatable;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class BlobTranslatableBlock implements TranslatableBlock {
    @NotNull
    private final String locale;
    @NotNull
    private final List<String> block;

    public static BlobTranslatableBlock of(@NotNull String locale,
                                           @NotNull List<String> block) {
        Objects.requireNonNull(locale, "Locale cannot be null");
        Objects.requireNonNull(block, "Block cannot be null");
        return new BlobTranslatableBlock(locale, block);
    }

    private BlobTranslatableBlock(@NotNull String locale,
                                  @NotNull List<String> block) {
        this.locale = locale;
        this.block = block;
    }

    @Override
    @NotNull
    public String getLocale() {
        return locale;
    }

    @NotNull
    public List<String> get() {
        return block;
    }

    @Override
    @NotNull
    public TranslatableBlock modify(Function<String, String> function) {
        return new BlobTranslatableBlock(locale, block.stream().map(function)
                .map(function)
                .toList());
    }
}
