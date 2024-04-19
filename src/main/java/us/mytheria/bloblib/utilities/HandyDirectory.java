package us.mytheria.bloblib.utilities;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;

public record HandyDirectory(@NotNull File directory) {

    public static HandyDirectory of(@NotNull File file) {
        Objects.requireNonNull(file, "File cannot be null");
        if (!file.exists())
            throw new IllegalArgumentException("File does not exist");
        if (!file.isDirectory())
            throw new IllegalArgumentException("File is not a directory");
        return new HandyDirectory(file);
    }

    /**
     * Lists all directories in the directory.
     *
     * @return An array of directories.
     */
    public File[] listDirectories() {
        return directory.listFiles(File::isDirectory);
    }

    /**
     * Lists all files in the directory.
     *
     * @return An array of files.
     */
    public File[] listFiles() {
        return directory.listFiles(File::isFile);
    }

    /**
     * Lists all files in the directory with the specified extension.
     *
     * @param extension The extension to filter by.
     * @return An array of files.
     */
    public File[] listFiles(@NotNull String extension) {
        Objects.requireNonNull(extension, "Extension cannot be null");
        return directory.listFiles((dir, name) -> name.endsWith(extension));
    }

    /**
     * Lists all files in the directory with the specified extensions.
     *
     * @param extensions The extensions to filter by.
     * @return An array of files.
     */
    public Collection<File> listRecursively(@NotNull String... extensions) {
        return FileUtils.listFiles(directory, extensions, true);
    }

    /**
     * Deletes the directory and all its contents.
     *
     * @param ifError The consumer to accept the exception if an error occurs.
     * @return {@code true} if the directory was deleted successfully, {@code false} otherwise.
     */
    public boolean deleteRecursively(@Nullable Consumer<IOException> ifError) {
        Path directory = this.directory.toPath();
        try (var walk = Files.walk(directory)) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            return true;
        } catch (IOException exception) {
            if (ifError != null)
                ifError.accept(exception);
            return false;
        }
    }

    /**
     * Deletes the directory and all its contents.
     *
     * @return {@code true} if the directory was deleted successfully, {@code false} otherwise.
     */
    public boolean deleteRecursively() {
        return deleteRecursively(null);
    }
}
