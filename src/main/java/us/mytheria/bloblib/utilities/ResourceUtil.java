package us.mytheria.bloblib.utilities;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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
     * Will only overwrite ConfigurationSections that
     * are not already present in the existing file
     * but are so in the temp file.
     * Comments will be overwritten by the new updated file.
     *
     * @param existingFile            the file to write to
     * @param updateYamlConfiguration the file to read from
     */
    public static void writeNewValues(File existingFile, YamlConfiguration updateYamlConfiguration) {
        FileConfiguration existingYamlConfig = YamlConfiguration.loadConfiguration(existingFile);
        Set<String> keys = updateYamlConfiguration.getConfigurationSection("").getKeys(true);
        keys.forEach(key -> {
            if (!updateYamlConfiguration.isConfigurationSection(key)) {
                try {
                    List<String> comments = updateYamlConfiguration.getComments(key);
                    List<String> inLine = updateYamlConfiguration.getInlineComments(key);
                    if (comments.size() > 0)
                        existingYamlConfig.setComments(key, comments);
                    if (inLine.size() > 0)
                        existingYamlConfig.setInlineComments(key, inLine);
                } catch (NoSuchMethodError ignored) {
                }
                // if it's not a section, it's a value
                if (existingYamlConfig.contains(key)) return;
            }
            if (existingYamlConfig.isConfigurationSection(key)) return; //if it exists, skip
            existingYamlConfig.set(key, updateYamlConfiguration.get(key)); // write
        });
        try {
            existingYamlConfig.save(existingFile);
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

    /**
     * Will update the existing file with the new values
     *
     * @param existingFile the file to update
     * @param main         the plugin
     */
    public static void updateYml(File existingFile, Plugin main) {
        String name = existingFile.getName();
        File path = existingFile.getParentFile();
        updateYml(path, "/temp" + name, name, existingFile, main);
    }

    public static void updateYml(File path, String tempFileName, String fileName, File existingFile, Plugin main) {
        if (!existingFile.exists())
            return;
        File tempFile = new File(path.getPath() + tempFileName);
        InputStream inputStream = main.getResource(fileName);
        if (inputStream == null)
            return;
        ResourceUtil.inputStreamToFile(tempFile, inputStream); // writes defaults to temp file
        YamlConfiguration tempYamlConfiguration = YamlConfiguration.loadConfiguration(tempFile);
        ResourceUtil.writeNewValues(existingFile,
                tempYamlConfiguration); // attempts to write new values to existing file if they don't exist
        tempFile.delete();
    }
}
