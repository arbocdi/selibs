package net.sf.selibs.tcp.nio.module;

import java.net.URI;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.constants.HMethods;
import net.sf.selibs.http.constants.HNames;
import net.sf.selibs.http.constants.HVersions;
import net.sf.selibs.http.io.HSerializer;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.tcp.nio.http.HttpProcessor;
import static net.sf.selibs.tcp.nio.module.HEchoClientProcessor.CLT_MSG_QUANTITY;
import static net.sf.selibs.tcp.nio.module.HEchoClientProcessor.MESSAGE_COUNTER;
import net.sf.selibs.utils.misc.SyncCounter;

/**
 * sends GET prefix?clt=connectionID&msg=msgNumber or POST
 * clt=connectionID&msg=msgNumber requests to server and verifies that response
 * contains sent data, first message isn't checked
 *
 * @author root
 */
public class AdvHEchoClientProcessor implements HttpProcessor {

    @Setter
    @Getter
    @Inject
    @Named(CLT_MSG_QUANTITY)
    protected Integer msgNum;
    @Getter
    @Setter
    @Inject
    protected SpeedCalculator counter;

    protected String method;
    protected String prefix;
    protected String host;

    public AdvHEchoClientProcessor(String method, String prefix, String host) {
        this.method = method;
        this.prefix = prefix;
        this.host = host;
    }

    @Override
    public void process(Connection con, HMessage response, Dispatcher dis) throws Exception {
        SyncCounter privateCounter = (SyncCounter) con.getAttachments().get(MESSAGE_COUNTER);
        if (privateCounter.get() != 0) {
            String respBody = new String(response.payload, "UTF-8");
            //System.out.println(respBody);
            if (!respBody.contains(String.format("clt=%s&msg=%s", con.getId(), privateCounter.get()))) {
                throw new RuntimeException("Recieved wrong response body " + respBody);
            }

        }
        this.counter.increment();
        privateCounter.increment();
        if (privateCounter.get() >= this.msgNum) {
            dis.close(con);
        } else {
            HMessage request = null;
            if (this.method.equals(HMethods.GET)) {
                //String uri = String.format("%s?clt=%s&msg=%s", prefix, con.getId(), privateCounter.get());
                //request = HMessage.createRequest(method, new URI(uri), host);
                request = this.createGetRequest(con.getId(), privateCounter.get());
            } else if (this.method.equals(HMethods.POST)) {
                //String msg = String.format("clt=%s&msg=%s", con.getId(), privateCounter.get());
                //request = HMessage.createRequest(HMethods.POST, HVersions.V11,
                //        new URI(prefix), host, msg.getBytes("UTF-8"));
                //request.addHeader(HNames.CONTENT_TYPE, "text/plain; charset=utf-8");
                request = this.createPostRequest(con.getId(), privateCounter.get());
            }
            dis.addWriteRequest(con, HSerializer.toByteArray(request));
        }
    }

    protected HMessage createGetRequest(int conId, int msgNum) throws Exception {
        String uri = String.format("%s?clt=%s&msg=%s", prefix, conId, msgNum);
        return HMessage.createRequest(HMethods.GET, new URI(uri), host);
    }

    protected HMessage createPostRequest(int conId, int msgNum) throws Exception {
        String msg = String.format("clt=%s&msg=%s", conId, msgNum);
        HMessage request = HMessage.createRequest(HMethods.POST, HVersions.V11,
                new URI(prefix), host, msg.getBytes("UTF-8"));
        request.addHeader(HNames.CONTENT_TYPE, "text/plain; charset=utf-8");
        return request;
    }

}
