package net.sf.selibs.tcp.nio;

import net.sf.selibs.tcp.nio.module.Dispatcher;
import java.io.IOException;

public class DispactherPool {

    public Dispatcher detDispatcher() throws IOException {
        return new Dispatcher();
    }
}
