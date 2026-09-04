package io.github.anjoismysign.bloblib.utility;

import io.github.anjoismysign.bloblib.api.BlobLibTranslatableAPI;
import io.github.anjoismysign.bloblib.manager.BlobLibConfigManager;
import io.github.anjoismysign.bloblib.translatable.TranslatableSnippet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TimeUtil {
    INSTANCE;

    private static final String DAY_IDENTIFIER = "TimeUtil.Day";
    private static final String HOUR_IDENTIFIER = "TimeUtil.Hour";
    private static final String MINUTE_IDENTIFIER = "TimeUtil.Minute";
    private static final String SECOND_IDENTIFIER = "TimeUtil.Second";

    public String parseTime(@NotNull String locale,
                            long milliseconds){
        String realLocale = BlobLibConfigManager.getInstance().getRealLocale(locale);
        return format(realLocale, milliseconds);
    }

    private String format(@NotNull String realLocale,
                          long milliseconds) {
        String daySnippet = snippet(DAY_IDENTIFIER, realLocale, "d");
        String hourSnippet = snippet(HOUR_IDENTIFIER, realLocale, "h");
        String minuteSnippet = snippet(MINUTE_IDENTIFIER, realLocale, "m");
        String secondSnippet = snippet(SECOND_IDENTIFIER, realLocale, "s");

        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 24;

        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(days).append(daySnippet).append(" ");
        }
        if (hours > 0) {
            result.append(hours).append(hourSnippet).append(" ");
        }
        if (minutes > 0) {
            result.append(minutes).append(minuteSnippet).append(" ");
        }
        result.append(seconds).append(secondSnippet);
        return result.toString();
    }

    private String snippet(@NotNull String identifier,
                           @NotNull String realLocale,
                           @NotNull String fallback) {
        @Nullable TranslatableSnippet snippet = BlobLibTranslatableAPI.getInstance()
                .getTranslatableSnippet(identifier, realLocale);
        if (snippet == null) {
            return fallback;
        }
        return snippet.get();
    }
}
