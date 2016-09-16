package net.sf.selibs.tcp.links;

import java.io.DataOutputStream;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.utils.chain.HException;
import net.sf.selibs.utils.chain.HLink;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class ChooserClient extends HLink<TCPMessage, Void> {

    @Getter
    @Setter
    @Element(name = "linkName")
    protected String ctxName;

    public ChooserClient(@Element(name = "linkName") String ctxName) {
        this.ctxName = ctxName;
    }

    @Override
    public Void handle(TCPMessage i) throws Exception {
        i.out = new DataOutputStream(i.out);
        ((DataOutputStream) i.out).writeUTF(ctxName);
        i.out.flush();
        return next.handle(i);
    }

    
}
