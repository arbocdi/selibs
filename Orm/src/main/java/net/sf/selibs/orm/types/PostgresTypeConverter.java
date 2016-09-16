/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.orm.types;

import java.util.Iterator;
import net.sf.selibs.orm.types.TypeMapping.DBTypes;

/**
 *
 * @author root
 */
public class PostgresTypeConverter implements TypeConverter {
    
    static TypeMapping postgreMapping = TypeMappingLoader.mappings.get("postgre");

    @Override
    public String getType(Class clazz) {
        DBTypes dbTypes = postgreMapping.javaToDB.get(clazz.getName());
        Iterator<String> i = dbTypes.dbTypes.iterator();
        if (i.hasNext()) {
            return i.next();
        }
        return null;
    }

}
