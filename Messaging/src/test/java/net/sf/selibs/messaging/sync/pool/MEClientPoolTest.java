package net.sf.selibs.messaging.sync.pool;

import java.io.File;
import net.sf.selibs.messaging.Message;
import net.sf.selibs.messaging.sync.pool.MEClientPool.MECSerializer;
import net.sf.selibs.messaging.sync.pool.MEServer.MESSerializer;
import net.sf.selibs.tcp.TCPConfig;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MEClientPoolTest {
    
    public static final File meServer = new File("meServer.xml");
    public static final File meCP = new File("meCP");
    public TCPConfig cfg;
    int poolSize;
    MESSerializer mesS;
    MECSerializer mecS;
    
    
    public MEClientPoolTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.ALL);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        cfg = new TCPConfig();
        cfg.backlog=1000;
        cfg.ip="127.0.0.1";
        cfg.port=2345;
        cfg.timeout=60000;
        poolSize = 5;
        
        for(File f:meCP.listFiles()){
            f.delete();
        }
        meCP.delete();
        
        mesS = new MESSerializer(meServer);
        mecS = new MECSerializer(meCP);
        
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void test() throws Exception{
        System.out.println("===========MEClientPoolTest:test==============");
        //write server&clients
        MEServer srv = MEServer.generate(cfg, poolSize,10000);
        mesS.save(srv);
        MEClientPool mec = MEClientPool.generate(cfg, poolSize,10000);
        mecS.save(mec);
        //read them
        MEServer srvL = mesS.load();
        MEClientPool mecL = mecS.load();
        srvL.start();
        mecL.start();
        Thread.sleep(1000);
        //exchange with echo
        for(int i=0;i<1000;i++){
            Message request = new Message();
            request.destination = MEServer.ECHO;
            request.source = "testClass";
            System.out.println(srvL.getMePool().exchange(request));
        }
        mecL.stop();
        srvL.stop();
        mecL.join();
        srvL.join();
    }
            
    
}
