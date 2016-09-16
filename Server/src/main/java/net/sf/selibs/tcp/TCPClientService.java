package net.sf.selibs.tcp;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.utils.inject.Injector;
import net.sf.selibs.utils.misc.Initializable;
import net.sf.selibs.utils.service.Service;
import net.sf.selibs.utils.service.ServiceException;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
@Slf4j
public class TCPClientService extends Service implements Initializable<Injector> {
//config=============

    @Element(name = "client")
    @Setter
    @Getter
    protected TCPClient client;

    public TCPClientService(@Element(name = "sleepInterval") int sleepInterval,
            @Element(name = "client") TCPClient client) {
        this.setSleepInterval(sleepInterval);
        this.client = client;
    }
    
    @Override
    protected void doStuff() {
        try {
            client.handle();
        } catch (Exception ex) {
            log.warn(String.format("Exception occured while running client %s", client.getCfg()), ex);
            client.close();
        }
    }

    @Override
    protected void postAction() {
        client.close();
    }

    @Override
    public void init() throws Exception {
        this.client.init();
    }

    @Override
    public void inject(Injector injector) throws Exception {
        this.client.inject(injector);
    }

}
