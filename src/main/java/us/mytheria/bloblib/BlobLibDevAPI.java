package us.mytheria.bloblib;

import org.bukkit.plugin.Plugin;
import us.mytheria.bloblib.entities.Result;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;
import java.io.IOException;

public class BlobLibDevAPI {
    private static final BlobLib main = BlobLib.getInstance();

    /**
     * @return The messages file
     */
    public static File getMessagesFile() {
        return main.getFileManager().messagesFile();
    }

    /**
     * @return The messages file path
     */
    public static String getMessagesFilePath() {
        return main.getFileManager().messagesFile().getPath();
    }

    /**
     * @return The sounds file
     */
    public static File getSoundsFile() {
        return main.getFileManager().soundsFile();
    }

    /**
     * @return The sounds file path
     */
    public static String getSoundsFilePath() {
        return main.getFileManager().soundsFile().getPath();
    }

    /**
     * @param fileName The name of the file
     * @param plugin   The plugin
     * @return The Result of the file. If the file didn't exist and no exceptions were found, Result will be valid.
     */
    public static Result<File> addDefaultMessagesFile(String fileName, Plugin plugin) {
        File path = new File(getMessagesFile() + "/" + plugin.getName());
        File file = new File(path +
                "/" + fileName + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                ResourceUtil.updateYml(path, "/temp" + fileName + ".yml", fileName + ".yml", file, plugin);
                return Result.valid(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Result.invalid(file);
    }

    /**
     * @param fileName The name of the file
     * @param plugin   The plugin
     * @return The Result of the file. If the file didn't exist and no exceptions were found, Result will be valid.
     */
    public static Result<File> addDefaultSoundsFile(String fileName, Plugin plugin) {
        File path = new File(getSoundsFile() + "/" + plugin.getName());
        File file = new File(path +
                "/" + fileName + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                ResourceUtil.updateYml(path, "/temp" + fileName + ".yml", fileName + ".yml", file, plugin);
                return Result.valid(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Result.invalid(file);
    }
}
