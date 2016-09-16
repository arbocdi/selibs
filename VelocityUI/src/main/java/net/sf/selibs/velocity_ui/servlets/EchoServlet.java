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
import net.sf.selibs.utils.io.FileUtils;
import net.sf.selibs.velocity_ui.VelocityUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.simpleframework.xml.Root;

@Root
public class EchoServlet extends HttpServlet {

    @Setter
    @Getter
    @Inject
    protected HNBConfig cfg;

    protected String echoTemplate;
    protected VelocityEngine ve;

    public EchoServlet() throws Exception {
        super();
        echoTemplate = new String(FileUtils.readFromCP(EchoServlet.class, "echo.html"), "UTF-8");
        ve = VelocityUtils.createEngine();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        VelocityContext ctx = new VelocityContext();
        ctx.put("echo", req.getRequestURI());
        StringWriter sw = new StringWriter();
        this.ve.evaluate(ctx, sw, "echo", this.echoTemplate);
        this.cfg.writeResponse(req,resp, sw.toString());
    }

}
