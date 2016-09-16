package net.sf.selibs.utils.inject;

import net.sf.selibs.utils.graph.Node;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
public class Wheel {

    @Inject
    Manufacturer m;
    @Node
    public Map<String, Bolt> bolts = new HashMap();
    @Inject
    @Node
    public Car car;
}
