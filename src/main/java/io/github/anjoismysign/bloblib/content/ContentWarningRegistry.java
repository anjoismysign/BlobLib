package io.github.anjoismysign.bloblib.content;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Collects every {@link ContentWarning} raised while loading content.
 * <p>
 * The registry is filled during asset loading and read once everything has
 * loaded, so that a server admin gets one report instead of a warning scattered
 * through startup.
 */
public enum ContentWarningRegistry {
    INSTANCE;

    private static final DateTimeFormatter FILE_STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private final List<ContentWarning> warnings = new ArrayList<>();

    @NotNull
    public static ContentWarningRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a warning.
     *
     * @param warning The warning to register
     */
    public void register(@NotNull ContentWarning warning) {
        Objects.requireNonNull(warning, "'warning' cannot be null");
        synchronized (warnings) {
            warnings.add(warning);
        }
    }

    /**
     * Drops every warning. Meant to be called when a reload begins, so that a
     * fixed file stops being reported.
     */
    public void clear() {
        synchronized (warnings) {
            warnings.clear();
        }
    }

    /**
     * @return Every warning raised so far, in the order they were raised.
     */
    @NotNull
    public List<ContentWarning> getWarnings() {
        synchronized (warnings) {
            return List.copyOf(warnings);
        }
    }

    /**
     * @return How many warnings have been raised.
     */
    public int size() {
        synchronized (warnings) {
            return warnings.size();
        }
    }

    /**
     * @return true if no warning has been raised.
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Builds the report, grouping warnings by the file they were found in.
     *
     * @return The report, one entry per line.
     */
    @NotNull
    public List<String> report() {
        List<ContentWarning> snapshot = getWarnings();
        Map<String, List<ContentWarning>> byFile = new LinkedHashMap<>();
        for (ContentWarning warning : snapshot)
            byFile.computeIfAbsent(warning.filePath(), key -> new ArrayList<>()).add(warning);
        List<String> lines = new ArrayList<>();
        lines.add("BlobLib content warnings (" + snapshot.size() + ")");
        lines.add("These are not errors. Every listed field was accepted and then ignored,");
        lines.add("so editing it changes nothing in game.");
        byFile.forEach((filePath, fileWarnings) -> {
            lines.add("");
            lines.add(filePath);
            fileWarnings.forEach(warning -> lines.add("  - " + warning.asLine()));
        });
        return lines;
    }

    /**
     * Writes the report to a timestamped file inside the given directory.
     *
     * @param directory The directory to write the report into
     * @return The path of the written file
     * @throws IOException If the report could not be written
     */
    @NotNull
    public Path saveReport(@NotNull Path directory) throws IOException {
        Objects.requireNonNull(directory, "'directory' cannot be null");
        Files.createDirectories(directory);
        Path path = directory.resolve("content-warnings_" + FILE_STAMP.format(LocalDateTime.now()) + ".txt");
        Files.write(path, report(), StandardCharsets.UTF_8);
        return path;
    }
}
