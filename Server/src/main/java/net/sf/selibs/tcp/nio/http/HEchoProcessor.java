package net.sf.selibs.tcp.nio.http;

import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.constants.HVersions;
import net.sf.selibs.http.io.HSerializer;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.tcp.nio.module.Dispatcher;
import org.simpleframework.xml.Root;
@Root
public class HEchoProcessor implements HttpProcessor{

    @Override
    public void process(Connection con, HMessage request, Dispatcher dis) throws Exception {
        HMessage response = HMessage.createResponse(HVersions.V11,200,"OK",HSerializer.toByteArray(request));
        dis.addWriteRequest(con, HSerializer.toByteArray(response));
    }
    
}
