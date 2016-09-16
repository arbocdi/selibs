package net.sf.selibs.messaging.beans.generator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import java.io.FileInputStream;
import net.sf.selibs.utils.misc.UHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class InterfaceImplementorTest {
    
    CompilationUnit interfaceCU;

    public InterfaceImplementorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception{
        
        FileInputStream in = new FileInputStream("generator/AuthRemote.java");
        try {
            // parse the file
            interfaceCU = JavaParser.parse(in);
        } finally {
            UHelper.close(in);
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testMakeImpl() throws Exception {
        System.out.println("==========ClientBeanGeneratorTest:testMakeImpl=========");
        InterfaceImplementor gen = new InterfaceImplementor();
        BeanMEImplementor mClient = new BeanMEImplementor();
        gen.setMethodImplementor(mClient);
        gen.setFieldAdder(mClient);
        System.out.println(gen.makeImpl(this.interfaceCU));
    }

}
