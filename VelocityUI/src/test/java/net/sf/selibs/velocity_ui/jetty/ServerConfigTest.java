/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.velocity_ui.jetty;

import java.io.File;
import net.sf.selibs.http.constants.HMethods;
import net.sf.selibs.utils.inject.Injector;
import net.sf.selibs.utils.misc.WSClient;
import org.apache.log4j.BasicConfigurator;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author root
 */
public class ServerConfigTest {

    public ServerConfigTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSLST() throws Exception {
        System.out.println("========ServerConfigTest:testSLST==========");
        //BasicConfigurator.configure();
        
        ServerConfig cfg = ServerConfig.generateDefault();
        File testDir = new File("testConfig");
        testDir.mkdirs();
        Serializer persister = new Persister();
        File jettyXml = new File(testDir, "jetty.xml");
        persister.write(cfg, jettyXml);
        cfg = persister.read(ServerConfig.class, jettyXml);

        Server srv = cfg.createServer(new Injector());
        srv.start();
        Thread.sleep(1000);
        String response = new String(WSClient.makeRequest("http://127.0.0.1:2121/app/hello", HMethods.GET));
        System.out.println(response);
        Assert.assertTrue(response.contains("Hello World!"));
        srv.stop();
        srv.join();
        
    }

}
