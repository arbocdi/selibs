package net.sf.selibs.tcp.nio.module;

import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.io.HSerializer;
import net.sf.selibs.tcp.TCPConfig;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.tcp.nio.CountingConnectionListener;
import net.sf.selibs.tcp.nio.http.HttpBridge;
import net.sf.selibs.tcp.nio.http.HttpProcessor;
import static net.sf.selibs.tcp.nio.module.HEchoClientProcessor.MESSAGE_COUNTER;
import static net.sf.selibs.tcp.nio.module.HEchoClientProcessor.CLT_MSG_QUANTITY;
import net.sf.selibs.utils.inject.Injector;
import net.sf.selibs.utils.misc.SyncCounter;
import net.sf.selibs.utils.service.AbstractService;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class ClientModule implements AbstractService {

    @Element
    @Getter
    protected NModule clients;
    @Element
    protected HMessage request;
    @Element
    @Setter
    @Getter
    protected int clientCount = 10;
    @Element
    @Setter
    @Getter
    protected int clientMessageQuantity = 10;
    @Element
    @Setter
    @Getter
    protected int iterationCount = 10;
    @Element
    @Setter
    @Getter
    protected int sleepMaxInterval = 10;
    @Getter
    protected SpeedCalculator speedCalc = new SpeedCalculator();
    @Getter
    protected Injector i = new Injector();

    

    public static ClientModule generateHttp(TCPConfig cfg, HttpProcessor proc, HMessage request) {
        ClientModule cm = new ClientModule();
        cm.clients = NModule.generateClient(cfg);

        HttpBridge http = new HttpBridge();
        http.httpProc = proc;
        cm.clients.setProcessor(http);
        cm.clients.getService().getDispatcher().setListener(new CountingConnectionListener());

        cm.request = request;

        return cm;
    }

    @Override
    public void start() throws Exception {
        i.addBinding(SpeedCalculator.class, speedCalc);
        i.addBinding(Integer.class, CLT_MSG_QUANTITY, (Integer) this.clientMessageQuantity);
        
        this.clients.inject(i);
        this.clients.init();
        this.clients.start();

    }

    public void startTest() throws Exception {
        try {
            this.speedCalc.setStartTime(System.currentTimeMillis());
            for (int iteration = 1; iteration <= this.iterationCount; iteration++) {
                this.addConnections();
                while (this.speedCalc.getValue()<this.clientCount*this.clientMessageQuantity*iteration) {
                    System.out.println("Messages processed = " + this.speedCalc.getValue());
                    System.out.println("Average speed = "+this.speedCalc.getAverageSpeed(System.currentTimeMillis()));
                    Thread.sleep(1000);
                }
            }
            System.out.println(String.format("Total messages processed %s",this.speedCalc.getValue()));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            this.stop();
        }
    }

    protected void addConnections() throws Exception {
        Random rnd = new Random();
        for (int cltNum = 1; cltNum <=this.clientCount; cltNum++) {
            Thread.sleep(rnd.nextInt(this.sleepMaxInterval));
            //create new connection
            Connection con = new Connection();
            con.getAttachments().put(MESSAGE_COUNTER, new SyncCounter());
            //add 1st request
            ((Connector) this.clients.getService()).addConnectRequest(con);
            this.clients.getService().getDispatcher().addWriteRequest(con, HSerializer.toByteArray(request));
        }
    }

    @Override
    public void stop() {
        this.clients.stop();
    }

    @Override
    public void join() throws InterruptedException {
        this.clients.join();
    }
    
    

}
