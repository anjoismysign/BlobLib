package us.mytheria.bloblib.utilities;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
     * @param existingFile         The file to be overwritten
     * @param newYamlConfiguration The new configuration to be written to the file
     */
    public static void writeNewValues(File existingFile, YamlConfiguration newYamlConfiguration) {
        FileConfiguration existingFileConfiguration = YamlConfiguration.loadConfiguration(existingFile);
        for (String section : newYamlConfiguration.getConfigurationSection("").getKeys(true)) {
            if (existingFileConfiguration.get(section) != null) continue;

            existingFileConfiguration.set(section, newYamlConfiguration.get(section));
        }

        try {
            existingFileConfiguration.save(existingFile);
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
}
