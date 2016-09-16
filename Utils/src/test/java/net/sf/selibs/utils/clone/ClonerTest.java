package net.sf.selibs.utils.clone;

import net.sf.selibs.utils.cloning.Cloner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
public class ClonerTest {
    
    Person dhurva;
    
    public ClonerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.dhurva = new Person();
        this.dhurva.name="dhurva";
        this.dhurva.age=9;
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testClone() throws Exception{
        System.out.println("============Cloner:clone============");
        Person p2 = (Person) Cloner.clone(dhurva);
        System.out.println(p2);
        Assert.assertEquals(dhurva, p2);
    }
    @Test
    public void testCloneNotCloneable() throws Exception{
        System.out.println("============Cloner:testCloneNotCloneable============");
        NCPerson person = new NCPerson();
        person.name = "arboc";
        NCPerson p2 = (NCPerson) Cloner.clone(person);
        Assert.assertSame(person, p2);
    }
    
}
