package net.sf.selibs.utils.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class IOUtils {

    public static byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf = new byte[32];
        int len = 0;
        while ((len = in.read(buf)) > 0) {
            bout.write(buf, 0, len);
        }
        return bout.toByteArray();
    }

    public static String readFully(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int c = rd.read();
            if (c < 0) {
                break;
            }
            sb.append((char)c);
        }
        return sb.toString();
    }
}
