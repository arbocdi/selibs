package net.sf.selibs.messaging.sync.pool;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.messaging.sync.handlers.EchoHandler;
import net.sf.selibs.messaging.sync.handlers.HandlerMap;
import net.sf.selibs.messaging.sync.handlers.MessageHandler;
import net.sf.selibs.messaging.sync.tcp.Responder;
import net.sf.selibs.tcp.TCPClient;
import net.sf.selibs.tcp.TCPClientService;
import net.sf.selibs.tcp.TCPConfig;
import net.sf.selibs.tcp.links.BufferedLink;
import net.sf.selibs.tcp.links.ChooserClient;
import net.sf.selibs.tcp.links.ObjectLink;
import net.sf.selibs.tcp.links.StreamsLink;
import net.sf.selibs.utils.chain.HChain;
import net.sf.selibs.utils.inject.ISerializer;
import net.sf.selibs.utils.inject.Injector;
import net.sf.selibs.utils.misc.USerializer;
import net.sf.selibs.utils.service.AbstractService;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class MEClientPool implements AbstractService {

    @Setter
    @Getter
    protected List<TCPClientService> clients = new LinkedList();
    @Setter
    @Getter
    protected Injector injector;

    public static MEClientPool generate(TCPConfig cfg, int poolSize, int sleepInterval) {
        MEClientPool clients = new MEClientPool();
        for (int i = 0; i < poolSize; i++) {
            HChain chain = new HChain();
            chain.add(new StreamsLink());
            chain.add(new BufferedLink());
            chain.add(new ChooserClient(String.format(MEServer.INITIATOR_N, i)));
            ObjectLink ol = new ObjectLink();
            ol.setNext(new Responder());
            chain.add(ol);
            TCPClient clt = new TCPClient(cfg, chain.getFirst());
            TCPClientService cltService = new TCPClientService(sleepInterval, clt);
            clients.clients.add(cltService);
        }
        HandlerMap hm = new HandlerMap();
        hm.addHandler(new EchoHandler(), MEServer.ECHO);
        clients.injector = new Injector();
        clients.injector.addBinding(MessageHandler.class, hm);
        return clients;
    }

    public HandlerMap getHM() {
        return (HandlerMap) this.injector.getBinding(MessageHandler.class);
    }

    @Override
    public void start() throws Exception {
        for (TCPClientService cs : this.clients) {
            cs.inject(injector);
            cs.start();
        }
    }

    @Override
    public void stop() {
        for (TCPClientService cs : this.clients) {
            cs.stop();
        }
    }

    @Override
    public void join() throws InterruptedException {
        for (TCPClientService cs : this.clients) {
            cs.join();
        }

    }

    public static class MECSerializer extends USerializer<MEClientPool, File> {

        protected ISerializer is;
        protected Serializer persister = new Persister();

        public MECSerializer(File storage) {
            super(storage);
            this.is = new ISerializer(this.getInjectorXML());
            this.storage.mkdirs();
        }

        public File getInjectorXML() {
            return new File(this.storage, "mec_injector.xml");
        }

        public File getClientNXML(int n) {
            return new File(this.storage, String.format("mec_client%s.xml", n));
        }

        @Override
        public void save(MEClientPool object) throws Exception {
            this.is.save(object.injector);
            int i = 0;
            for (TCPClientService cs : object.clients) {
                this.persister.write(cs, this.getClientNXML(i));
                i++;
            }
        }

        @Override
        public MEClientPool load() throws Exception {
            MEClientPool mec = new MEClientPool();
            mec.injector = is.load();
            for (File f : this.storage.listFiles()) {
                if (f.getName().matches("mec_client\\d+\\.xml")) {
                    mec.clients.add(this.persister.read(TCPClientService.class, f));
                }
            }
            return mec;
        }

    }

}
