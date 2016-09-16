package net.sf.selibs.utils.inject;

import net.sf.selibs.utils.graph.Node;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import lombok.Data;

@Data
public class Car {
    @Inject
    protected Driver driver;
    @Node
    public List<Wheel> wheels = new LinkedList();
}
