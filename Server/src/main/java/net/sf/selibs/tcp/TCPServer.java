package net.sf.selibs.tcp;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.net.ServerSocketFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.tcp.factory.DefaultServerSocketFactory;
import net.sf.selibs.tcp.factory.ThreadPoolFactory;
import net.sf.selibs.tcp.links.TCPMessage;
import net.sf.selibs.utils.chain.HChain;
import net.sf.selibs.utils.chain.Handler;
import net.sf.selibs.utils.graph.GraphUtils;
import net.sf.selibs.utils.graph.Init;
import net.sf.selibs.utils.inject.Injector;
import net.sf.selibs.utils.misc.Initializable;
import net.sf.selibs.utils.misc.UHelper;
import net.sf.selibs.utils.service.Service;
import net.sf.selibs.utils.service.ServiceException;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
@Slf4j
public class TCPServer extends Service implements Initializable<Injector> {

    //config==================
    @Element(name = "cfg")
    @Getter
    @Setter
    protected TCPConfig cfg;
    @Element(name = "threadPoolFactory")
    @Getter
    @Setter
    protected ThreadPoolFactory tpf;
    @Element(name = "handler")
    @Getter
    @Setter
    protected Handler<TCPMessage, Void> handler;
    @Getter
    @Setter
    @Element
    protected ServerSocketFactory ssf = new DefaultServerSocketFactory();
    //work====================
    @Getter
    protected ServerSocket socket;
    @Getter
    protected ThreadPoolExecutor tpe;

    public TCPServer(@Element(name = "cfg") TCPConfig cfg,
            @Element(name = "threadPoolFactory") ThreadPoolFactory tpf,
            @Element(name = "handler") Handler<TCPMessage, Void> handler) {
        this.cfg = cfg;
        this.tpf = tpf;
        this.handler = handler;
    }

    @Override
    public void start() throws ServiceException {
        try {
            this.socket = ssf.createServerSocket();
            TCPUtils.bindServerSocket(socket, cfg);
            tpe = tpf.produce();
            super.start();
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    @Override
    protected void doStuff() {
        Socket client = null;
        try {
            client = this.socket.accept();
            if (cfg.timeout != null) {
                client.setSoTimeout(cfg.timeout);
            }
            this.tpe.submit(new TCPTask(handler, new TCPMessage(client)));
        } catch (Exception ex) {
            log.trace("Cant accept client connection", ex);
            UHelper.close(client);
        }
    }

    @Override
    protected void postAction() {
        UHelper.close(socket);
        tpe.shutdownNow();
    }

    @Override
    public void init() throws Exception {
        GraphUtils.invokeAnnotatedMethods(this.handler, Init.class);
    }

    @Override
    public void inject(Injector injector) throws Exception {
        injector.injectIntoGraph(this.handler);
    }

    @Slf4j
    public static class TCPTask implements Runnable {

        protected Handler<TCPMessage, Void> handler;
        protected TCPMessage msg;

        public TCPTask(Handler handler, TCPMessage msg) {
            this.handler = handler;
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                log.trace(String.format("Opened connection to %s", msg.socket));
                handler.handle(msg);
            } catch (Exception ex) {
                log.trace(String.format("Error occured while working with %s", msg.socket), ex);
            } finally {
                log.trace(String.format("Closing connection to %s", msg.socket));
                UHelper.close(msg.in);
                UHelper.close(msg.out);
                UHelper.close(msg.socket);
            }
        }

    }

    @Override
    public void stop() {
        super.stop();
        UHelper.close(socket);
        this.tpe.shutdownNow();
    }

    public void join() throws InterruptedException {
        this.worker.join();
        this.tpe.awaitTermination(1, TimeUnit.DAYS);
    }

}
