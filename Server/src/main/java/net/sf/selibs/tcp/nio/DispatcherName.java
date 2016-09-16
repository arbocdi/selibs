package net.sf.selibs.tcp.nio;

import net.sf.selibs.utils.locator.ImmServiceName;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class DispatcherName extends ImmServiceName {

    public DispatcherName(@Element(name = "name") String name) {
        super(name);
    }
    
}
