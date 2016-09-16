package net.sf.selibs.velocity_ui.servlets;

import java.io.IOException;
import java.io.StringWriter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.tcp.nio.module.Dispatcher;
import net.sf.selibs.utils.io.FileUtils;
import net.sf.selibs.velocity_ui.VelocityUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.simpleframework.xml.Root;

@Root
public class ConnectionsServlet extends HttpServlet {

    @Inject
    @Getter
    protected Dispatcher dis;
    @Setter
    @Getter
    @Inject
    protected HNBConfig cfg;
    //work
    protected String connectionsTemplate;
    protected VelocityEngine ve;

    public ConnectionsServlet() throws Exception {
        this.connectionsTemplate = new String(FileUtils.readFromCP(ConnectionsServlet.class, "connections.html"), "UTF-8");
        ve = VelocityUtils.createEngine();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        VelocityContext ctx = new VelocityContext();
        ctx.put("connections", this.getDis().getConnections());
        StringWriter sw = new StringWriter();
        ve.evaluate(ctx, sw, ConnectionsServlet.class.getName(), this.connectionsTemplate);
        this.cfg.writeResponse(req,resp, sw.toString());
    }

}
