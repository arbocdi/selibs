package net.sf.selibs.utils.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.sf.selibs.utils.misc.UHelper;

public class FileUtils {

    public static byte[] readToByteArray(String file) throws IOException {
        InputStream in = null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            in = new BufferedInputStream(new FileInputStream(file));
            while (true) {
                int bt = in.read();
                if (bt == -1) {
                    break;
                }
                bout.write(bt);
            }
            return bout.toByteArray();
        } finally {
            UHelper.close(in);
        }
    }

    public static void writeByteArray(String file, byte[] data) throws IOException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            out.write(data);
        } finally {
            UHelper.close(out);
        }
    }

    public static void delete(File f, boolean keepDirs) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c, keepDirs);
            }
        }
        if ((f.isDirectory() && !keepDirs) || f.isFile()) {
            f.delete();
        }
    }

    public static byte[] readFromCP(Class clazz, String name) throws IOException {
        InputStream in = null;
        try {
            in = clazz.getResourceAsStream(name);
            return IOUtils.readFully(in);
        } finally {
            UHelper.close(in);
        }
    }
}
