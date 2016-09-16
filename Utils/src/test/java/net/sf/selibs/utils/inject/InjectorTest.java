package net.sf.selibs.utils.inject;

import java.io.File;
import net.sf.selibs.utils.inject.Injector;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.ToString;
import net.sf.selibs.utils.locator.ImmServiceName;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

public class InjectorTest {

    Injector injector;

    String object = "Hello";
    String namedObject = "Named Hello";
    String globalObject = "Global Hello";

    public InjectorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.injector = new Injector();
        this.injector.addBinding(String.class, object);
        this.injector.addBinding(String.class, "name", namedObject);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testInject() throws Exception {
        System.out.println("===============Injector:testInject===========");
        SomeClass instance = new SomeClass();
        this.injector.injectInto(instance);
        System.out.println(instance);
        Assert.assertSame(this.object, instance.string);
        Assert.assertSame(this.namedObject, instance.namedString);
        Assert.assertNotNull(instance.sName);
    }

    @Test
    public void testSaveLoad() throws Exception {
        System.out.println("==================Injector:testSaveLoad===================");
        SomeClass instance = new SomeClass();
        instance.string = "test string";
        File xml = new File("injector.xml");
        Injector injector = new Injector();
        injector.addBinding(SomeClass.class, instance);
        injector.addBinding(SomeInterface.class, instance);
        ISerializer s = new ISerializer(xml);
        s.save(injector);
        Injector injector2 = s.load();
        Assert.assertSame(injector2.getBinding(SomeInterface.class), injector2.getBinding(SomeClass.class));
    }

    public static interface SomeInterface {

    }

    @ToString
    @Root
    public static class SomeClass implements SomeInterface {

        @Inject
        @Element
        public String string;
        @Inject
        @Named("name")
        public String namedString;
        @Inject
        public ImmServiceName sName = new ImmServiceName("someService");

    }

}
