package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Result;
import org.bson.Document;

import java.io.File;
import java.util.Map;

public interface BlobObject {
    String getKey();

    File saveToFile();

    Document getData();

    default void saveMap(String key, Map<String, String> map) {
        Document innerData = new Document();
        innerData.putAll(map);
        getData().put(key, innerData);
    }

    default <T extends Map<String, String>> Result<T> getMap(String key, Class<T> clazz) {
        Document innerData = getData().get(key, Document.class);
        T instance;
        try {
            instance = clazz.getConstructor().newInstance();
            innerData.forEach((entryKey, entryValue) -> {
                if (entryValue instanceof String)
                    instance.put(entryKey, (String) entryValue);
            });
            return Result.valid(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.invalidBecauseNull();
        }
    }
}
