package us.mytheria.bloblib.utilities;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class ResourceUtil {
    public static void moveResource(File file, InputStream inputStream) {
        try {
            if (!file.getParentFile().exists()) {
                File parentFile = file.getParentFile();
                parentFile.mkdirs();
            }
            if (!file.exists())
                file.createNewFile();
            else
                return;
            FileOutputStream fos = new FileOutputStream(file);
            byte[] ba = inputStream.readAllBytes();
            fos.write(ba);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param newFile               the file to write to
     * @param tempYamlConfiguration the file to read from
     */
    public static void writeNewValues(File newFile, YamlConfiguration tempYamlConfiguration) {
        FileConfiguration existingYamlConfig = YamlConfiguration.loadConfiguration(newFile);
        Set<String> keys = tempYamlConfiguration.getConfigurationSection("").getKeys(true);
        keys.forEach(key -> {
            if (!tempYamlConfiguration.isConfigurationSection(key)) return; // if it's not a section, it's a value
            if (existingYamlConfig.isConfigurationSection(key)) return; //if it exists, skip
            existingYamlConfig.set(key, existingYamlConfig.get(key)); // write
        });
        try {
            tempYamlConfiguration.save(newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void inputStreamToFile(File file, InputStream inputStream) {
        try {
            if (!file.exists())
                file.createNewFile();
            else
                return;
            FileOutputStream fos = new FileOutputStream(file);
            byte[] ba = inputStream.readAllBytes();
            fos.write(ba);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateYml(File path, String tempFileName, String fileName, File existingFile, Plugin main) {
        File tempFile = new File(path.getPath() + tempFileName);
        ResourceUtil.inputStreamToFile(tempFile, main.getResource(fileName)); // writes defaults to temp file
        YamlConfiguration tempYamlConfiguration = YamlConfiguration.loadConfiguration(tempFile);
        ResourceUtil.writeNewValues(existingFile,
                tempYamlConfiguration); // attempts to write new values to existing file if they don't exist
        tempFile.delete();
    }
}
