package net.sf.selibs.tcp.nio.module;

import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.RequestLine;
import net.sf.selibs.http.io.HSerializer;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.tcp.nio.http.HttpProcessor;
import net.sf.selibs.tcp.nio.module.Dispatcher;
import net.sf.selibs.utils.misc.SyncCounter;

/**
 *
 * @author root
 */
public class HEchoClientProcessor implements HttpProcessor {

    public static final String MESSAGE_COUNTER = "messageCounter";
    public static final String CLT_MSG_QUANTITY = "messageNumber";

    @Setter
    @Getter
    protected HMessage request;
    @Setter
    @Getter
    @Inject
    @Named(CLT_MSG_QUANTITY)
    protected Integer msgNum;
    @Getter
    @Setter
    @Inject
    protected SpeedCalculator counter;

    public HEchoClientProcessor(HMessage request) {
        this.request = request;
    }

    @Override
    public void process(Connection con, HMessage response, Dispatcher dis) throws Exception {
        String respBody = new String(response.payload, "UTF-8");
        if (!respBody.contains(((RequestLine) request.line).url.toString())) {
            throw new RuntimeException("Recieved wrong response body " + respBody);
        }
        this.counter.increment();
        SyncCounter privateCounter = (SyncCounter) con.getAttachments().get(MESSAGE_COUNTER);
        privateCounter.increment();
        if (privateCounter.get() >= this.msgNum) {
            dis.close(con);
        } else {
            dis.addWriteRequest(con, HSerializer.toByteArray(request));
        }
    }

}
