package net.sf.selibs.messaging;

import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class BalancedMETest {

    MEPool balanced;
    MessageExchanger me1;
    MessageExchanger me2;

    public BalancedMETest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        balanced = new MEPool();
        me1 = Mockito.mock(MessageExchanger.class);
        me2 = Mockito.mock(MessageExchanger.class);
        this.balanced.addMessageExchanger(me1);
        this.balanced.addMessageExchanger(me2);
        error = false;
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetNext() {
        System.out.println("========BalancedME:getNext=========");
        Assert.assertEquals(me1, this.balanced.getNext());
        Assert.assertEquals(me2, this.balanced.getNext());
        Assert.assertEquals(me1, this.balanced.getNext());
        Assert.assertEquals(me2, this.balanced.getNext());
    }
    boolean error;
    @Test
    public void testGetNextMultithreaded(){
        System.out.println("=======BalancedME:testGetNextMultithreaded============");
        final List<MessageExchanger> exchangers = new LinkedList();
        exchangers.add(me1);
        exchangers.add(me2);
        Thread t1 = new Thread(){
          public void run(){
             try{
                 for(int i=0;i<100;i++){
                     Assert.assertTrue(exchangers.contains(balanced.getNext()));
                     Thread.sleep(2);
                 }
                 
             } 
             catch(Throwable t){
                 t.printStackTrace();
                 error = true;
             }
          }  
        };
        t1.start();
        Thread t2 = new Thread(){
          public void run(){
             try{
                 for(int i=0;i<70;i++){
                     Assert.assertTrue(exchangers.contains(balanced.getNext()));
                     Thread.sleep(3);
                 }
                 
             } 
             catch(Throwable t){
                 t.printStackTrace();
                 error = true;
             }
          }  
        };
        t2.start();
         Thread t3 = new Thread(){
          public void run(){
             try{
                 for(int i=0;i<50;i++){
                     Assert.assertTrue(exchangers.contains(balanced.getNext()));
                     Thread.sleep(4);
                 }
                 
             } 
             catch(Throwable t){
                 t.printStackTrace();
                 error = true;
             }
          }  
        };
        t3.start();
        Assert.assertFalse(error);
    }
  

}
