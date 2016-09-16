package net.sf.selibs.http.servlet;

import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.constants.HCodes;
import net.sf.selibs.http.constants.HVersions;
import net.sf.selibs.http.io.HSerializer;
import org.simpleframework.xml.Root;

@Root
public class EchoServlet implements HServlet {

    @Override
    public HMessage handle(HMessage i) throws Exception {
        HMessage response = HMessage.createResponse(HVersions.V11,
                HCodes.OK,
                "OK",
                HSerializer.toByteArray(i));
        return response;
    }

   
}
