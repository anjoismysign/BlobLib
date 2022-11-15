package us.mytheria.bloblib.entities;

import java.io.*;

public class UpdatableSerializable implements Serializable {
    private int version;

    private Serializable value;

    public static byte[] serialize(UpdatableSerializable updatableSerializable) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(updatableSerializable);
            byte b[] = bos.toByteArray();
            out.close();
            bos.close();
            return b;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UpdatableSerializable deserialize(byte[] bytes) {
        UpdatableSerializable updatableSerializable;
        if (bytes == null)
            return null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            updatableSerializable = (UpdatableSerializable) in.readObject();
            return updatableSerializable;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UpdatableSerializable(int valueVersion, Serializable value) {
        this.version = valueVersion;
        this.version = valueVersion;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Serializable getValue() {
        return value;
    }

    public void setValue(Serializable value) {
        this.value = value;
    }
}
