package net.sf.selibs.tcp.links;

import java.io.DataInputStream;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.utils.chain.Handler;
import net.sf.selibs.utils.graph.Node;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

@Root
public class ChooserHandler implements Handler<TCPMessage, Void> {

    @ElementMap
    @Setter
    @Getter
    @Node
    protected Map<String, Handler<TCPMessage, Void>> handlers = new HashMap();

    public void addHandler(String name, Handler handler) {
        this.handlers.put(name, handler);
    }

    @Override
    public Void handle(TCPMessage i) throws Exception {
        i.in = new DataInputStream(i.in);
        String ctxName = ((DataInputStream) i.in).readUTF();
        Handler<TCPMessage, Void> nextHandler = handlers.get(ctxName);
        return nextHandler.handle(i);

    }

   
}
