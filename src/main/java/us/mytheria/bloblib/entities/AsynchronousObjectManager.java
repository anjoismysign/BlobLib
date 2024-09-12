package us.mytheria.bloblib.entities;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.managers.ManagerDirector;
import us.mytheria.bloblib.utilities.HandyDirectory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

public class AsynchronousObjectManager<T extends BlobObject> extends ObjectManager<T> {
    private final Function<File, T> readFunction;

    /**
     * Constructor for ObjectManager
     *
     * @param managerDirector    The manager director
     * @param loadFilesDirectory The directory to load files from
     * @param supplier           The supplier
     * @param fileSupplier       The file supplier
     * @param parent             The ObjectDirector parent
     */
    public AsynchronousObjectManager(ManagerDirector managerDirector, File loadFilesDirectory, Supplier<Map<String, T>> supplier, Supplier<Map<String, File>> fileSupplier, ObjectDirector<T> parent, Function<File, T> readFunction) {
        super(managerDirector, loadFilesDirectory, supplier, fileSupplier, parent);
        this.readFunction = readFunction;
    }

    public void loadFiles(File path, CompletableFuture<Void> mainFuture) {
        if (!path.exists())
            path.mkdir();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            HandyDirectory handyDirectory = HandyDirectory.of(path);
            Collection<File> files = handyDirectory.listRecursively("yml");
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            files.forEach(file -> {
                CompletableFuture<Void> fileFuture = CompletableFuture.runAsync(() -> {
                    loadFile(file, mainFuture::completeExceptionally);
                });
                futures.add(fileFuture);
            });
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenAccept(v -> mainFuture.complete(null));
        });
    }

    @Override
    public void loadFile(@NotNull File file,
                         Consumer<Throwable> ifFail) {
        T blobObject;
        try {
            blobObject = readFunction.apply(file);
            if (blobObject != null) {
                if (blobObject.edit() != null)
                    getParent().setObjectIsEditable(true);
                this.addObject(blobObject.getKey(), blobObject, file);
            }
        } catch (Throwable throwable) {
            Bukkit.getLogger().log(Level.SEVERE, throwable.getMessage() + " \n " +
                    "At: " + file.getPath(), throwable);
            ifFail.accept(throwable);
        }
    }
}
