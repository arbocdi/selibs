package net.sf.selibs.messaging;

import java.util.LinkedList;
import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
@Root
public class MEPool implements MessageExchanger {
    @ElementList
    protected List<MessageExchanger> exchangers = new LinkedList();
    protected int counter = 0;

    public void addMessageExchanger(MessageExchanger me) {
        this.exchangers.add(me);
    }

    public synchronized MessageExchanger getNext() {
        MessageExchanger me = this.exchangers.get(counter);
        counter++;
        counter = counter % exchangers.size();
        return me;
    }

    @Override
    public Message exchange(Message request) throws Exception {
        MessageExchanger me = this.getNext();
        return me.exchange(request);
    }

}
