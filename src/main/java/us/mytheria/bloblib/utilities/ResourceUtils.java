package us.mytheria.bloblib.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceUtils {
    public void moveResource(File file, InputStream inputStream) {
        try {
            if (!file.getParentFile().exists()) {
                File parentFile = file.getParentFile();
                parentFile.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            } else
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
