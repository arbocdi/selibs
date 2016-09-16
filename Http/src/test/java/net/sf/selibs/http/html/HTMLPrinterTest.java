package net.sf.selibs.http.html;

import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author root
 */
public class HTMLPrinterTest {
    
    HTMLPrinter printer;
    List<Person> persons;
    
    public HTMLPrinterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        printer = new HTMLPrinter();
        printer.setFields(Person.class.getDeclaredFields());
        printer.setTitle("Persons");
        
        persons = new LinkedList();
        
        Person dh = new Person();
        dh.name = "dhurva";
        dh.age = 9;
        
        Person arb = new Person();
        arb.name = "selibs";
        arb.age = 7;
        
        persons.add(dh);
        persons.add(arb);
        
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testGenerateDocument() throws Exception {
        System.out.println("===========HTMLPrinter:testGenerateDocument============");
        String html = this.printer.generateDocument(persons);
        System.out.println(html);
        Assert.assertTrue(html.contains("dhurva"));
        Assert.assertTrue(html.contains("selibs"));
        Assert.assertTrue(html.contains("9"));
        Assert.assertTrue(html.contains("7"));
        
        
    }
    
}
