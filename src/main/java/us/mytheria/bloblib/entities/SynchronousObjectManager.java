package us.mytheria.bloblib.entities;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.managers.ManagerDirector;
import us.mytheria.bloblib.utilities.HandyDirectory;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

public class SynchronousObjectManager<T extends BlobObject> extends ObjectManager<T> {
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
    public SynchronousObjectManager(ManagerDirector managerDirector, File loadFilesDirectory, Supplier<Map<String, T>> supplier, Supplier<Map<String, File>> fileSupplier, ObjectDirector<T> parent, Function<File, T> readFunction) {
        super(managerDirector, loadFilesDirectory, supplier, fileSupplier, parent);
        this.readFunction = readFunction;
        reload();
    }

    @Override
    public void reload() {
        if (readFunction == null)
            return;
        super.reload();
    }

    public void loadFiles(File path, CompletableFuture<Void> mainFuture) {
        if (!path.exists())
            path.mkdir();
        HandyDirectory handyDirectory = HandyDirectory.of(path);
        Collection<File> files = handyDirectory.listRecursively("yml");
        files.forEach(file -> {
            loadFile(file, mainFuture::completeExceptionally);
        });
        mainFuture.complete(null);
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
