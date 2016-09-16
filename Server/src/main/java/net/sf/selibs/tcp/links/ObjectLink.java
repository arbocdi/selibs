package net.sf.selibs.tcp.links;

import java.io.ObjectInputStream;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.utils.chain.HLink;
import net.sf.selibs.utils.io.streams.OOSWrapper;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class ObjectLink extends HLink<TCPMessage, Void> {

    @Element(required = false)
    @Setter
    @Getter
    protected Integer byteLimit;

    @Override
    public Void handle(TCPMessage i) throws Exception {
        i.out = new OOSWrapper(i.out);
        if (this.byteLimit != null) {
            ((OOSWrapper) i.out).setLimit(byteLimit);
        }
        i.in = new ObjectInputStream(i.in);
        return this.next.handle(i);

    }
}
