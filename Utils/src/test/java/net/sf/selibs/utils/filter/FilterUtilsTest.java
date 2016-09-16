package net.sf.selibs.utils.filter;


import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FilterUtilsTest {
    
    List<String> elements;
    final String  etalon="Dhurva";
    public FilterUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        elements = new LinkedList();
        elements.add("Dhurva");
        elements.add("Dhurva");
        elements.add("Ivan");

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testApplyFilter() {
        System.out.println("=========testApplyFilter===========");
        List<String> data = FilterUtils.applyFilter(elements, new FilterI(etalon));
        System.out.println(elements);
        System.out.println(etalon);
        System.out.println(data);
        for(String element:data){
            Assert.assertEquals(etalon, element);
        }
    }
    @Test
    public void testFindFirst() {
        System.out.println("============testFindFirst==============");
        String data = FilterUtils.<String>findFirst(elements, new FilterI(etalon));
        Assert.assertEquals(data, etalon);
    }
    
    @Test
    public void testFindFirstIndex() {
        System.out.println("============testFindFirstIndex==============");
        int index = FilterUtils.findFirstIndex(elements, new FilterI("Ivan"));
        Assert.assertEquals(2, index);
        index = FilterUtils.findFirstIndex(elements, new FilterI("arboc"));
        Assert.assertEquals(-1, index);
    }

    public static class FilterI implements Filter<String> {

        protected String etalon;

        public FilterI(String etalon) {
            this.etalon = etalon;
        }

        @Override
        public boolean applicable(String t) {
            return t.equals(etalon);
        }

    }

}
