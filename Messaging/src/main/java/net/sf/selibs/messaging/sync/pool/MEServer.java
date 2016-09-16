package net.sf.selibs.messaging.sync.pool;

import java.io.File;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.messaging.MEPool;
import net.sf.selibs.messaging.Pinger;
import net.sf.selibs.messaging.sync.SME;
import net.sf.selibs.messaging.sync.tcp.Initiator;
import net.sf.selibs.tcp.TCPConfig;
import net.sf.selibs.tcp.TCPServer;
import net.sf.selibs.tcp.factory.ThreadPoolFactory;
import net.sf.selibs.tcp.links.BufferedLink;
import net.sf.selibs.tcp.links.ChooserHandler;
import net.sf.selibs.tcp.links.ObjectLink;
import net.sf.selibs.tcp.links.StreamsLink;
import net.sf.selibs.utils.chain.HChain;
import net.sf.selibs.utils.misc.USerializer;
import net.sf.selibs.utils.service.AbstractService;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;

@Root
public class MEServer implements AbstractService {

    public static final String INITIATOR_N = "Initiator%s";
    public static final String ECHO = "echo";
    public static final String SERVER = "meServer";

    @Getter
    @Setter
    @Element
    protected TCPServer srv;
    @Getter
    @Setter
    @Element
    protected MEPool mePool;
    @Getter
    @Setter
    @Element
    protected Pinger pinger;

    public static MEServer generate(TCPConfig cfg, int poolSize,int interval) {

        MEServer meServer = new MEServer();
        meServer.pinger = new Pinger(interval);
        meServer.pinger.setDestination(ECHO);
        meServer.pinger.setSource(SERVER);
        meServer.mePool = new MEPool();
        meServer.pinger.setMe(meServer.mePool);

        HChain chain = new HChain();
        chain.add(new StreamsLink());
        chain.add(new BufferedLink());
        ChooserHandler chooser = new ChooserHandler();
        chain.add(chooser);

        for (int i = 0; i < poolSize; i++) {
            Initiator init = new Initiator();
            SME me = new SME();
            init.setMe(me);
            ObjectLink ol = new ObjectLink();
            ol.setNext(init);
            meServer.mePool.addMessageExchanger(me);
            chooser.addHandler(String.format(INITIATOR_N, i), ol);
        }

        ThreadPoolFactory tpf = new ThreadPoolFactory();
        tpf.maxThreads = poolSize * 2;
        tpf.minThreads = poolSize;
        meServer.srv = new TCPServer(cfg, tpf, chain.getFirst());
        return meServer;
    }

    @Override
    public void start() throws Exception {
        this.srv.start();
        this.pinger.start();
    }

    @Override
    public void stop() {
        this.pinger.stop();
        this.srv.stop();
    }

    @Override
    public void join() throws InterruptedException {
        this.srv.join();
        this.pinger.join();
    }

    public static class MESSerializer extends USerializer<MEServer, File> {

        protected Serializer persister = new Persister(new CycleStrategy());

        public MESSerializer(File storage) {
            super(storage);
        }

        @Override
        public void save(MEServer object) throws Exception {
            persister.write(object, this.storage);
        }

        @Override
        public MEServer load() throws Exception {
            return persister.read(MEServer.class, this.storage);
        }

    }

}
