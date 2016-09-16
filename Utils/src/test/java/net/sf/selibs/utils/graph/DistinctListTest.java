package net.sf.selibs.utils.graph;

import net.sf.selibs.utils.graph.DistinctList;
import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import net.sf.selibs.utils.inject.Driver;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author root
 */
public class DistinctListTest {

    List list;
    Driver d1;
    Driver d2;

    public DistinctListTest() {
    }

    @Before
    public void setUp() {
        list = new DistinctList();
        d1 = new Driver("dhurva");
        d2 = new Driver("arboc");
    }

    @Test
    public void testAdd() {
        System.out.println("============DistinctList:TestAdd==============");
        Assert.assertTrue(list.add(d1));
        Assert.assertTrue(list.add(d2));
        Assert.assertFalse(list.add(d1));
        Assert.assertFalse(list.add(d2));

        List l2 = new LinkedList();
        l2.add(d1);
        l2.add(d1);
        Assert.assertFalse(list.addAll(l2));

        System.out.println(list);
        Assert.assertEquals(2, list.size());
        Assert.assertSame(list.get(0), d1);
        Assert.assertSame(list.get(1), d2);
    }

}
