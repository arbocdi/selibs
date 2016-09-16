/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.tcp.nio.module;

import net.sf.selibs.tcp.nio.module.Dispatcher;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.tcp.nio.ConnectionListener;
import net.sf.selibs.tcp.nio.Processor;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author root
 */
public class DispatcherTest {

    Dispatcher dispatcher;
    SocketChannel sc;
    Connection con;
    SelectionKey key;

    public DispatcherTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.configure();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        this.dispatcher = Mockito.spy(new Dispatcher());
        Mockito.doNothing().when(this.dispatcher).close(Mockito.any(Connection.class));
        this.dispatcher.selector = Mockito.mock(Selector.class);
        this.dispatcher.listener = Mockito.mock(ConnectionListener.class);
        this.dispatcher.processor = Mockito.mock(Processor.class);
        sc = Mockito.mock(SocketChannel.class);
        con = new Connection(sc);
        key = Mockito.mock(SelectionKey.class);
        Mockito.when(key.channel()).thenReturn(sc);
        key.attach(con);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of write method, of class Dispatcher.
     */
    @Test
    public void testWrite() throws Exception {
        System.out.println("write");

        ByteBuffer bb = ByteBuffer.allocate(1);
        bb.put((byte) 1);
        bb.flip();
        bb.get();
        con.getQueue().offer(bb);
        this.dispatcher.write(key);
        Mockito.verify(key).interestOps(SelectionKey.OP_READ);
        Mockito.verify(sc).write(bb);
        Assert.assertTrue(con.getQueue().isEmpty());

    }
   @Test
    public void testWriteSoftClose() throws Exception {
        System.out.println("==============testWriteSoftClose==============");
        ByteBuffer bb = ByteBuffer.allocate(0);
        bb.flip();
        con.getQueue().offer(bb);
        this.dispatcher.write(key);
        Mockito.verify(this.dispatcher).close(con);
    }


@Test
    public void testRead() throws Exception {
        System.out.println("==========testRead=============");
        final byte[] data = {1,2,3};
        Mockito.when(sc.read(Mockito.any(ByteBuffer.class))).thenAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                ByteBuffer bb=(ByteBuffer) args[0];
                bb.put(data);
                return data.length;
            }
        });
        this.dispatcher.read(key);
        Mockito.verify(this.dispatcher.processor).process(Mockito.eq(data),Mockito.eq(con),Mockito.eq(this.dispatcher));
    }

    /**
     * Test of addWriteRequest method, of class Dispatcher.
     */
    @Test
    public void testAddWriteRequest() {
        System.out.println("addWriteRequest");
        Connection con = null;

    }

    /**
     * Test of close method, of class Dispatcher.
     */
    @Test
    public void testClose() {
        System.out.println("close");
        Connection con = null;
    }

    /**
     * Test of closeAllConnections method, of class Dispatcher.
     */
    @Test
    @Ignore
    public void testCloseAllConnections() {
        System.out.println("closeAllConnections");
        Set<SelectionKey> set = new HashSet();
        set.add(key);
        Mockito.when(this.dispatcher.selector.keys()).thenReturn(set);
        this.dispatcher.closeAllConnections();
        Mockito.verify(this.dispatcher).close(con);

    }

    /**
     * Test of post method, of class Dispatcher.
     */
    @Test
    public void testPost() {
        System.out.println("post");

    }

}
