package net.sf.selibs.orm.map;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
@Data
public class Record {

    protected Map<String, Object> values = new HashMap();

    public Object add(String name, Object value) {
        return this.values.put(name, value);
    }
    
    
    
    
}
