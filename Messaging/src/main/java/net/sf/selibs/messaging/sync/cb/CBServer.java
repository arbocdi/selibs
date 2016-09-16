package net.sf.selibs.messaging.sync.cb;

import java.io.File;
import lombok.Getter;
import net.sf.selibs.messaging.MessageExchanger;
import net.sf.selibs.messaging.Pinger;
import net.sf.selibs.messaging.sync.SME;
import net.sf.selibs.messaging.sync.handlers.EchoHandler;
import net.sf.selibs.messaging.sync.handlers.HandlerMap;
import net.sf.selibs.messaging.sync.handlers.MessageHandler;
import net.sf.selibs.messaging.sync.tcp.Initiator;
import net.sf.selibs.messaging.sync.tcp.Responder;
import net.sf.selibs.tcp.TCPConfig;
import net.sf.selibs.tcp.TCPServer;
import net.sf.selibs.tcp.factory.ThreadPoolFactory;
import net.sf.selibs.tcp.links.BufferedLink;
import net.sf.selibs.tcp.links.ChooserHandler;
import net.sf.selibs.tcp.links.ObjectLink;
import net.sf.selibs.tcp.links.StreamsLink;
import net.sf.selibs.utils.graph.GraphUtils;
import net.sf.selibs.utils.inject.ISerializer;
import net.sf.selibs.utils.inject.Injector;
import net.sf.selibs.utils.misc.USerializer;
import net.sf.selibs.utils.service.AbstractService;
import net.sf.selibs.utils.service.ServiceException;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class CBServer implements AbstractService {

    //constants===
    public static final String INITIATOR = "initiator";
    public static final String RESPONDER = "responder";
    public static final String ECHO = "echo";

    @Getter
    protected TCPServer server;
    @Getter
    protected Pinger pinger;
    @Getter
    protected Injector injector = new Injector();

    @Override
    public void start() throws ServiceException {
        try {
            injector.injectInto(this.pinger);
            this.server.inject(injector);
            this.server.init();
            this.server.start();
            this.pinger.start();
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    @Override
    public void stop() {
        this.server.stop();
        this.pinger.stop();
    }

    @Override
    public void join() throws InterruptedException {
        this.server.getWorker().join();
        this.pinger.getWorker().join();
    }

    public static CBServer generate(TCPConfig cfg, int pingInterval) {
        CBServer cbSrv = new CBServer();

        HandlerMap hm = new HandlerMap();
        hm.addHandler(new EchoHandler(), CBServer.ECHO);

        SME me = new SME();

        cbSrv.injector.addBinding(SME.class, me);
        cbSrv.injector.addBinding(MessageExchanger.class, me);
        cbSrv.injector.addBinding(MessageHandler.class, hm);

        StreamsLink streams = new StreamsLink();
        BufferedLink buffered = new BufferedLink();
        streams.setNext(buffered);
        ChooserHandler chooser = new ChooserHandler();
        buffered.setNext(chooser);

        ObjectLink initObject = new ObjectLink();
        initObject.setNext(new Initiator());
        chooser.addHandler(INITIATOR, initObject);

        ObjectLink respObject = new ObjectLink();
        respObject.setNext(new Responder());
        chooser.addHandler(RESPONDER, respObject);

        cbSrv.server = new TCPServer(cfg, new ThreadPoolFactory(), streams);

        cbSrv.pinger = new Pinger(pingInterval);
        cbSrv.pinger.setDestination(CBServer.ECHO);

        return cbSrv;
    }

    public SME getME() {
        return this.injector.getBinding(SME.class);
    }

    public HandlerMap getHM() {
        return (HandlerMap) this.injector.getBinding(MessageHandler.class);
    }

    public static class CBSSerializer extends USerializer<CBServer, File> {

        ISerializer injectorSerializer;

        public CBSSerializer(File source) {
            super(source);
            injectorSerializer = new ISerializer(this.getInjectorXml());
        }

        @Override
        public void save(CBServer object) throws Exception {
            storage.mkdirs();
            Serializer persister = new Persister();
            GraphUtils.getFirstFromGraph(object.server.getHandler(),Initiator.class).setMe(null);
            GraphUtils.getFirstFromGraph(object.server.getHandler(),Responder.class).setMh(null);
            object.pinger.setMe(null);
            
            persister.write(object.server, this.getServerXml());
            persister.write(object.pinger, this.getpingerXml());
            injectorSerializer.save(object.injector);

        }

        @Override
        public CBServer load() throws Exception {
            CBServer srv = new CBServer();
            Serializer persister = new Persister();
            srv.server = persister.read(TCPServer.class, this.getServerXml());
            srv.pinger = persister.read(Pinger.class, this.getpingerXml());
            srv.injector = injectorSerializer.load();
            return srv;

        }

        public File getServerXml() {
            return new File(storage, "cbServer.xml");
        }

        public File getpingerXml() {
            return new File(storage, "cbPinger.xml");
        }

        public File getInjectorXml() {
            return new File(storage, "cbInjector.xml");
        }

    }
}
