package net.sf.selibs.messaging.sync.handlers;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import net.sf.selibs.utils.graph.Node;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
@Data
public abstract class HandlerChain implements MessageHandler {

    @Element
    @Node
    protected MessageHandler next;

    public List<MessageHandler> getChainLinks() {
        List<MessageHandler> links = new LinkedList();
        MessageHandler next = this;
        while (true) {
            if (next == null) {
                break;
            }
            links.add(next);
            if (!(next instanceof HandlerChain)) {
                break;
            }
            next = ((HandlerChain) next).getNext();
        }
        return links;
    }

    public void init() {
        if (this.next != null) {
            this.next.init();
        }
    }

}
