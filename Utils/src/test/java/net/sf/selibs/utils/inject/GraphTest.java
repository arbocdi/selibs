package net.sf.selibs.utils.inject;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import net.sf.selibs.utils.graph.GraphUtils;
import org.junit.Before;
import org.junit.Test;

public class GraphTest {

    Injector injector;
    CarGraph graph;

    @Before
    public void before() {
        graph = new CarGraph();
        injector = new Injector();
        this.injector.addBinding(Driver.class, graph.driver);
        this.injector.addBinding(Manufacturer.class, graph.m);
        this.injector.addBinding(Manufacturer.class, "bm", graph.bm);
    }

    @Test
    public void testInjectIntoGraph() throws Exception {
        System.out.println("===============GraphTest:testInjectIntoGraph===============");
        Car car = graph.car;
        injector.injectIntoGraph(car);
        List objects = new LinkedList();
        GraphUtils.graphToList(car, objects);
        for (Object o : objects) {
            System.out.println(o);
        }
        Assert.assertSame(graph.driver, car.driver);
        for (Wheel wheel : graph.wheels) {
            Assert.assertSame(graph.m, wheel.m);
            Assert.assertSame(graph.car, wheel.car);
        }
        for (Bolt bolt : graph.bolts.values()) {
            Assert.assertSame(graph.bm, bolt.getM());
        }

    }
}
