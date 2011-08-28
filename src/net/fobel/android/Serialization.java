package net.fobel.android;

import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.IOException;

public class Serialization {
    public static byte[] serializeObject(Object o) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(o);
            out.close();
            // Get the bytes of the serialized object
            byte[] buf = bos.toByteArray();
            return buf;
        } catch(IOException ioe) {
            // Toast.makeText(this, "Error during serialization",
            // Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static Object deserializeObject(byte[] b) {
        try {
            ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(b));
            Object object = in.readObject();
            in.close();

            return object;
        } catch(ClassNotFoundException cnfe) {
            // Toast.makeText(this,
            // "Error deserializeObject: class not found error",
            // Toast.LENGTH_SHORT).show();
            return null;
        } catch(IOException ioe) {
            // Toast.makeText(this, "Error deserializeObject: io error",
            // Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static byte[] read_bytes(FileInputStream in) throws IOException {
        int length = in.available();
        int offset = 0;
        byte[] buf = new byte[length];
        while(offset < length) {
            int count = in.read(buf, offset, (length - offset));
            offset += count;
        }
        in.close();
        return buf;
    }
}
