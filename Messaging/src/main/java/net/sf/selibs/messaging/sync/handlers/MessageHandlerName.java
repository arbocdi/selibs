package net.sf.selibs.messaging.sync.handlers;

import net.sf.selibs.utils.locator.ImmServiceName;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class MessageHandlerName extends ImmServiceName {

    public MessageHandlerName(@Element(name = "name")String name) {
        super(name);
    }
    
}
