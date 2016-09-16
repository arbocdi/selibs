package net.sf.selibs.utils.cloning;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import net.sf.selibs.utils.misc.UHelper;

public class Cloner {

    public static Object clone(Serializable s) throws IOException, ClassNotFoundException {
        NotCloneable not = s.getClass().getAnnotation(NotCloneable.class);
        if (not != null) {
            return s;
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = null;
        ObjectInputStream oin = null;
        try {
            oout = new ObjectOutputStream(bout);
            oout.writeObject(s);
            oin = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
            return oin.readObject();
        } finally {
            UHelper.close(oout);
            UHelper.close(oin);
        }
    }
}
