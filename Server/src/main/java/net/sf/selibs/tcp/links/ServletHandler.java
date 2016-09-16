package net.sf.selibs.tcp.links;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.RequestLine;
import net.sf.selibs.http.constants.HCodes;
import net.sf.selibs.http.constants.HNames;
import net.sf.selibs.http.constants.HValues;
import net.sf.selibs.http.constants.HVersions;
import net.sf.selibs.http.io.HSerializer;
import net.sf.selibs.http.servlet.HServlet;
import net.sf.selibs.utils.chain.HChain;
import net.sf.selibs.utils.chain.Handler;
import net.sf.selibs.utils.graph.Node;
import net.sf.selibs.utils.misc.UHelper;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
@Slf4j
public class ServletHandler implements Handler<TCPMessage, Void> {

    @Element(name = "servlet")
    @Getter
    @Setter
    @Node
    protected HServlet servlet;

    public ServletHandler(@Element(name = "servlet") HServlet servlet
    ) {
        this.servlet = servlet;
    }

    @Override
    public Void handle(TCPMessage i) throws Exception {
        while (true) {
            HMessage request = null;
            try {
                request = HSerializer.fromStream(i.in);
            } catch (Exception ex) {
                log.warn(String.format("Cant process request"), ex);
                HMessage response = HMessage.createResponse(HVersions.V11,
                        HCodes.WRONG_REQUEST,
                        "Wrong Request",
                        UHelper.getStackTrace(ex).getBytes("UTF-8"));
                HSerializer.toStream(i.out, response);
                return null;
            }
            HMessage response = null;
            try {
                response = this.servlet.handle(request);
                if (request.getConnectionClose()) {
                    response.addHeader(HNames.CONNECTION, HValues.CLOSE);
                }

            } catch (Exception ex) {
                log.warn(String.format("Cant process request %s", request), ex);
                response = HMessage.createResponse(HVersions.V11,
                        HCodes.INTERNAL_ERROR,
                        "Internal Error",
                        UHelper.getStackTrace(ex).getBytes("UTF-8"));
                HSerializer.toStream(i.out, response);
                return null;
            }
            HSerializer.toStream(i.out, response);
            if (!request.line.getVersion().equals(HVersions.V11)
                    || response.getConnectionClose()) {
                return null;
            }
        }

    }

}
