package us.mytheria.bloblib.entities.message;

import org.bson.Document;
import us.mytheria.bloblib.entities.DocumentDecorator;

import javax.annotation.Nullable;
import java.io.*;

public record BlobPluginMessage(String key, Document value) implements Serializable {

    public static byte[] serialize(BlobPluginMessage message) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(message);
            byte[] b = bos.toByteArray();
            out.close();
            bos.close();
            return b;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static BlobPluginMessage deserialize(byte[] bytes) {
        BlobPluginMessage message;
        if (bytes == null)
            return null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in;
        try {
            in = new ObjectInputStream(bis);
            message = (BlobPluginMessage) in.readObject();
            return message;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DocumentDecorator getValueAsDecorator() {
        return new DocumentDecorator(value);
    }
}
