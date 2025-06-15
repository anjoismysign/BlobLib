package io.github.anjoismysign.bloblib.utilities;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

public class BlobUtil {

    public static InputStream blobToInputStream(Blob blob) {
        InputStream inputStream;
        try {
            inputStream = blob.getBinaryStream();
            return inputStream;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                blob.free();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Blob byteArrayOutputStreamToBlob(ByteArrayOutputStream out) {
        try {
            return new SerialBlob(out.toByteArray());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
