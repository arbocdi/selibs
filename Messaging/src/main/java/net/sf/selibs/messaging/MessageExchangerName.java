package net.sf.selibs.messaging;

import net.sf.selibs.utils.locator.ImmServiceName;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class MessageExchangerName extends ImmServiceName {

    public MessageExchangerName(@Element(name = "name") String name) {
        super(name);
    }

}
