package us.mytheria.bloblib.entities.message;

import javax.annotation.Nullable;
import java.io.*;

public class BungeeMessage implements Serializable {

    public static byte[] serialize(BungeeMessage message) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(message);
            byte b[] = bos.toByteArray();
            out.close();
            bos.close();
            return b;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static BungeeMessage deserialize(byte[] bytes) {
        BungeeMessage message;
        if (bytes == null)
            return null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            message = (BungeeMessage) in.readObject();
            return message;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String key;
    private String type;
    private Serializable value;

    public BungeeMessage(String key, String type, Serializable value) {
        this.key = key;
        this.type = type;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public Serializable getValue() {
        return value;
    }
}
