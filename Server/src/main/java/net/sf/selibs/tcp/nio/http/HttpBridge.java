package net.sf.selibs.tcp.nio.http;



import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.http.nio.ParserContext;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.tcp.nio.module.Dispatcher;
import net.sf.selibs.tcp.nio.Processor;
import net.sf.selibs.utils.graph.Node;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Slf4j
@Root
public class HttpBridge implements Processor {

    public static final String PARSER_CONTEXT = "parserCtx";
    @Element
    @Node
    public HttpProcessor httpProc;

    @Override
    public void process(byte[] data, Connection con, Dispatcher dis) {
        ParserContext ctx = (ParserContext) con.getAttachments().get(PARSER_CONTEXT);
        if (ctx == null) {
            ctx = new ParserContext();
            con.getAttachments().put(PARSER_CONTEXT, ctx);
        }
        try {
            if (ctx.parseBytes(data)) {
                this.httpProc.process(con, ctx.getMessage(), dis);
                con.getAttachments().remove(PARSER_CONTEXT);
            }
        } catch (Exception ex) {
            log.debug("Cant parse message for connection " + con, ex);
            dis.close(con);
        }
    }

}
