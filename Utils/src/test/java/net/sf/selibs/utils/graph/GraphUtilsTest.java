package net.sf.selibs.utils.graph;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import net.sf.selibs.utils.chain.TLink;
import net.sf.selibs.utils.inject.Bolt;
import net.sf.selibs.utils.inject.Car;
import net.sf.selibs.utils.inject.CarGraph;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author root
 */
public class GraphUtilsTest {

    CarGraph graph;

    public GraphUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        graph = new CarGraph();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testTreeToList() throws Exception {
        System.out.println("===============GraphTest:testTreeToList===============");
        Car car = graph.car;
        List objects = GraphUtils.graphToList(car);
        for (Object o : objects) {
            System.out.println(o);
        }
        Assert.assertEquals(6, objects.size());
        Assert.assertTrue(objects.contains(car));
        Assert.assertTrue(objects.containsAll(graph.bolts.values()));
        Assert.assertTrue(objects.containsAll(graph.wheels));

    }

    @Test
    public void testGetAnnotatedMethods() throws Exception {
        System.out.println("===============GraphTest:testGetAnnotatedMethods===============");
        List<Method> methods = GraphUtils.getAnnotatedMethods(SomeHandler.class, Init.class);
        System.out.println(methods);
        for (Method m : methods) {
            if (!m.getName().equals("initMethod1") && !m.getName().equals("initMethod2")) {
                Assert.fail("Wrong method name");

            }
        }
    }

    @Test
    public void testInvokeAnnotatedMethods() throws Exception {
        System.out.println("===============GraphTest:testInvokeAnnotatedMethods===============");
        SomeHandler handler = new SomeHandler();
        List handlers = new LinkedList();
        handlers.add(handler);
        GraphUtils.invokeAnnotatedMethods(handlers, Init.class);
        System.out.println(handler.events);
        Assert.assertEquals(2, handler.events.size());
        Assert.assertTrue(handler.events.contains("init1"));
        Assert.assertTrue(handler.events.contains("init2"));
    }

    @Test
    public void testGetAllMethods() throws Exception {
        System.out.println("===============GraphTest:testGetAllMethods===============");
        List<Method> methods = GraphUtils.getAllMethods(Shape2D.class);
        List<String> names = getMethodNames(methods);
        Assert.assertTrue(names.contains("getName"));
        Assert.assertTrue(names.contains("getPerimeter"));
    }

    @Test
    public void testNullNode() throws Exception {
        System.out.println("=============GraphTest:testNullNode===============");
        TLink l1 = new TLink("link1");
        TLink l2 = new TLink("link2");
        l1.setNext(l2);
        List links = new LinkedList();
        GraphUtils.graphToList(l1, links);
        Assert.assertEquals(2, links.size());
        Assert.assertTrue(links.contains(l1));
        Assert.assertTrue(links.contains(l2));
    }

    @Test
    public void testGetAllFields() throws Exception {
        System.out.println("===============GraphTest:testGetAllFields===============");
        List<Field> fields = GraphUtils.getAllFields(Shape2D.class);
        List<String> names = getFieldNames(fields);
        Assert.assertTrue(names.contains("name"));
        Assert.assertTrue(names.contains("perimeter"));
    }
    
    @Test
    public void testFirstFromGraph() throws Exception {
        System.out.println("===============GraphTest:testFirstFromGraph===============");
        Bolt bolt = GraphUtils.getFirstFromGraph(graph.car, Bolt.class);
        System.out.println(bolt);
        Assert.assertNotNull(bolt);
        Connection con = GraphUtils.getFirstFromGraph(graph.car, Connection.class);
        Assert.assertNull(con);
    }
        @Test
    public void testAllFromGraph() throws Exception {
        System.out.println("===============GraphTest:testAllFromGraph===============");
        List<Bolt> bolts = GraphUtils.getAllFromGraph(graph.car, Bolt.class);
        System.out.println(bolts);
        Assert.assertEquals(3, bolts.size());
        Assert.assertTrue(bolts.containsAll(graph.bolts.values()));
        
    }


    public List<String> getMethodNames(List<Method> methods) {
        List<String> names = new LinkedList();
        for (Method m : methods) {
            System.out.println(m);
            names.add(m.getName());
        }
        return names;
    }

    public List<String> getFieldNames(List<Field> methods) {
        List<String> names = new LinkedList();
        for (Field m : methods) {
            System.out.println(m);
            names.add(m.getName());
        }
        return names;
    }

}
