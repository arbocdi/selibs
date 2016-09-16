package net.sf.selibs.orm.types;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import net.sf.selibs.orm.JDBCUtils;
import net.sf.selibs.orm.types.TypeMapping.DbTypes;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class TypeMappingLoader {

    public static final Map<String, TypeMapping> mappings = new HashMap();

    static {
        InputStream in = null;
        Serializer persister = new Persister();
        try {
            in = TypeMappingLoader.class.getResourceAsStream("postgre.xml");
            mappings.put("postgre", persister.read(TypeMapping.class, in));

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            JDBCUtils.close(in);
        }
        //loadDbTypes
        

    }
}
