package net.sf.selibs.orm.cache;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.Getter;
import lombok.Setter;

import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.orm.Database;
import net.sf.selibs.orm.JDBCUtils;
import net.sf.selibs.orm.properties.ClassUtils;
import net.sf.selibs.utils.cache.Cache;
import net.sf.selibs.utils.cache.HCache;
import net.sf.selibs.utils.service.Service;
import org.simpleframework.xml.Root;

@Root
@Slf4j
public class DBSynchronizer extends Service {

    @Getter
    protected Cache<Class,Cache> cache = new HCache();
    @Setter
    @Getter
    protected Database db;
    @Setter
    @Getter
    protected DataSource ds;
    protected Class[] entities;

    public void setEntities(Class... entity) {
        cache.clear();
        this.entities = entity;
        for(Class e:entity){
            cache.put(e, new HCache());
        }
    }

    @Override
    protected void doStuff() {
        Connection con = null;
        try {
            con = ds.getConnection();
            db.setConnection(con);
            for (Class entity : this.entities) {
                try {
                    Map dataMap = new HashMap();
                    List data = db.findAll(entity);
                    for (Object o : data) {
                        dataMap.put(ClassUtils.getPkValue(o), o);
                    }
                    this.cache.get(entity).renew(dataMap);
                } catch (Exception ex) {
                    log.warn(String.format("Cant update cache for %s", entity), ex);
                }
            }
        } catch (Exception ex) {
            log.warn("Cant update DB data", ex);
        } finally {
            JDBCUtils.close(con);
        }
    }
}
