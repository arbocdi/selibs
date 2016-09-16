package net.sf.selibs.tcp.nio.http;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import lombok.Data;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.constants.HCodes;
import net.sf.selibs.http.constants.HNames;
import net.sf.selibs.http.constants.HValues;
import net.sf.selibs.http.constants.HVersions;

import net.sf.selibs.http.html.HTMLPrinter;
import net.sf.selibs.http.servlet.HServlet;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.tcp.nio.module.Dispatcher;
import net.sf.selibs.utils.chain.HException;
import org.simpleframework.xml.Root;

@Root
public class ConPrinterServlet implements HServlet {

    public static final String CONNECTION_PRINTER = "ConnectionPrinter";
    @Inject
    private Dispatcher dis;
    //work==============
    protected transient HTMLPrinter printer;

    public Dispatcher getDispatcher() {
        return this.dis;
    }

    public void setDispatcher(Dispatcher dis) {
        this.dis = dis;
    }

    public ConPrinterServlet() {
        this.printer = new HTMLPrinter();
        this.printer.setTitle("Established Connections");
        this.printer.setFields(ConEntity.class.getDeclaredFields());
    }

    @Override
    public HMessage handle(HMessage request) throws HException {
        try {
            HMessage response = null;
            String html = this.printer.generateDocument(this.convert(this.getDispatcher().getConnections()));
            response = HMessage.createResponse(HVersions.V11,
                    HCodes.OK,
                    "OK",
                    html.getBytes("UTF-8"));
            response.addHeader(HNames.CONTENT_TYPE, HValues.TEXT_HTML);
            return response;
        } catch (Exception ex) {
            throw new HException(ex);
        }
    }

    protected List<ConEntity> convert(Collection<Connection> connections) {
        List<ConEntity> ents = new LinkedList();
        for (Connection con : connections) {
            ents.add(this.convert(con));
        }
        Collections.sort(ents);
        return ents;
    }

    protected ConEntity convert(Connection con) {
        ConEntity ent = new ConEntity();
        ent.id = con.getId();
        ent.local = con.getLocalAddress();
        ent.remote = con.getRemoteAddress();
        return ent;
    }

}
