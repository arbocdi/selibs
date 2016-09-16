package net.sf.selibs.messaging.sync.cb;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import net.sf.selibs.messaging.MessageExchanger;
import net.sf.selibs.messaging.Pinger;
import net.sf.selibs.messaging.sync.SME;
import static net.sf.selibs.messaging.sync.cb.CBServer.ECHO;
import static net.sf.selibs.messaging.sync.cb.CBServer.INITIATOR;
import static net.sf.selibs.messaging.sync.cb.CBServer.RESPONDER;
import net.sf.selibs.messaging.sync.handlers.EchoHandler;
import net.sf.selibs.messaging.sync.handlers.HandlerMap;
import net.sf.selibs.messaging.sync.handlers.MessageHandler;
import net.sf.selibs.messaging.sync.tcp.Initiator;
import net.sf.selibs.messaging.sync.tcp.Responder;
import net.sf.selibs.tcp.TCPClient;
import net.sf.selibs.tcp.TCPClientService;
import net.sf.selibs.tcp.TCPConfig;
import net.sf.selibs.tcp.links.BufferedLink;
import net.sf.selibs.tcp.links.ChooserClient;
import net.sf.selibs.tcp.links.ObjectLink;
import net.sf.selibs.tcp.links.StreamsLink;
import net.sf.selibs.utils.chain.HChain;
import net.sf.selibs.utils.graph.GraphUtils;
import net.sf.selibs.utils.inject.ISerializer;
import net.sf.selibs.utils.inject.Injector;
import net.sf.selibs.utils.misc.USerializer;
import net.sf.selibs.utils.service.AbstractService;
import net.sf.selibs.utils.service.ServiceException;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class CBClient implements AbstractService {

    @Getter
    protected TCPClientService responder;
    @Getter
    protected TCPClientService initiator;
    @Getter
    protected Pinger pinger;
    @Getter
    protected Injector injector = new Injector();

    public static CBClient generate(TCPConfig cfg, int interval) {
        CBClient clt = new CBClient();

        SME me = new SME();

        HandlerMap hm = new HandlerMap();
        hm.addHandler(new EchoHandler(), CBServer.ECHO);

        clt.injector.addBinding(SME.class, me);
        clt.injector.addBinding(MessageExchanger.class, me);
        clt.injector.addBinding(MessageHandler.class, hm);

        clt.pinger = new Pinger(interval);
        clt.pinger.setDestination(ECHO);
        clt.pinger.setMe(me);

        HChain respChain = new HChain();
        respChain.add(new StreamsLink());
        respChain.add(new BufferedLink());
        respChain.add(new ChooserClient(INITIATOR));
        respChain.add(new ObjectLink());
        respChain.add(new Responder());

        TCPClient respClient = new TCPClient(cfg, respChain.getFirst());
        clt.responder = new TCPClientService(interval, respClient);

        HChain initChain = new HChain();
        initChain.add(new StreamsLink());
        initChain.add(new BufferedLink());
        initChain.add(new ChooserClient(RESPONDER));
        initChain.add(new ObjectLink());
        initChain.add(new Initiator());

        TCPClient initClient = new TCPClient(cfg, initChain.getFirst());
        clt.initiator = new TCPClientService(interval, initClient);

        return clt;
    }

    @Override
    public void start() throws Exception {

        this.responder.init();
        this.initiator.init();
        this.responder.inject(injector);
        this.initiator.inject(injector);

        injector.injectInto(pinger);

        this.responder.start();
        this.initiator.start();
        this.pinger.start();

    }

    @Override
    public void stop() {
        this.responder.stop();
        this.initiator.stop();
        this.pinger.stop();
    }

    @Override
    public void join() throws InterruptedException {
        this.responder.getWorker().join();
        this.initiator.getWorker().join();
        this.pinger.getWorker().join();
    }

    public HandlerMap getHM() {
        return (HandlerMap) this.injector.getBinding(MessageHandler.class);
    }

    public MessageExchanger getSME() {
        return this.injector.getBinding(SME.class);
    }

    public static class CBCSerializer extends USerializer<CBClient, File> {

        protected ISerializer iSerializer;

        public CBCSerializer(File storage) {
            super(storage);
            storage.mkdirs();
            this.iSerializer = new ISerializer(this.getInjectorXML());
        }

        @Override
        public void save(CBClient object) throws Exception {
            Serializer persister = new Persister();
            GraphUtils.getFirstFromGraph(object.responder.getClient().getHandler(), Responder.class).setMh(null);
            GraphUtils.getFirstFromGraph(object.initiator.getClient().getHandler(), Initiator.class).setMe(null);
            persister.write(object.responder, this.getResponderXML());
            persister.write(object.initiator, this.getInitiatorXML());
            persister.write(object.pinger, this.getPingerXML());
            this.iSerializer.save(object.injector);
        }

        @Override
        public CBClient load() throws Exception {
            CBClient client = new CBClient();
            Serializer persister = new Persister();
            client.responder = persister.read(TCPClientService.class, getResponderXML());
            client.initiator = persister.read(TCPClientService.class, getInitiatorXML());
            client.pinger = persister.read(Pinger.class, getPingerXML());
            client.injector = this.iSerializer.load();
            return client;
        }

        public File getResponderXML() {
            return new File(storage, "clientResponder.xml");
        }

        public File getInitiatorXML() {
            return new File(storage, "clientInitiator.xml");
        }

        public File getPingerXML() {
            return new File(storage, "clientPinger.xml");
        }

        public File getInjectorXML() {
            return new File(storage, "clientInjector.xml");
        }

    }
}
