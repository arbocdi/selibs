package net.sf.selibs.orm.db_types;

import java.io.InputStream;
import java.lang.reflect.Field;
import net.sf.selibs.orm.types.TypeMapping.DbTypes;
import net.sf.selibs.utils.misc.UHelper;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class DbTypesLoader {

    //dbNames
    private static DbTypes postgres;

    public static DbTypes getPostgres() {
        return postgres;
    }

    static {
        Serializer persister = new Persister();
        for (Field f : DbTypesLoader.class.getDeclaredFields()) {
            f.setAccessible(true);
            if (f.getType().equals(DbTypes.class)) {
                InputStream in = null;
                try {
                    String fileName = f.getName();
                    in = DbTypesLoader.class.getResourceAsStream(fileName + ".xml");
                    DbTypes dbTypes = persister.read(DbTypes.class, in);
                    f.set(DbTypesLoader.class, dbTypes);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    UHelper.close(in);
                }

            }
        }

    }

}
