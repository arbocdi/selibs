package net.sf.selibs.velocity_ui.servlets;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.utils.io.FileUtils;
import net.sf.selibs.velocity_ui.VelocityUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class InfoServlet extends HttpServlet {

    public static final int MB = 1024 * 1024;

    @Setter
    @Getter
    @Inject
    protected HNBConfig cfg;
    @Setter
    @Getter
    @Element(name = "path")
    protected String path;

    protected String infoTemplate;
    protected VelocityEngine ve;

    public InfoServlet(@Element(name = "path") String path) throws Exception {
        super();
        infoTemplate = new String(FileUtils.readFromCP(EchoServlet.class, "info.html"), "UTF-8");
        ve = VelocityUtils.createEngine();
        this.path = path;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String value = req.getParameter("action");
        if ("gc".equals(value)) {
            System.gc();
        }
        //add runtime data
        Runtime rt = Runtime.getRuntime();
        KeyValuePair totalMemory = new KeyValuePair("total memory [MB]", String.valueOf(rt.totalMemory() / MB));
        KeyValuePair freeMemory = new KeyValuePair("free memory [MB]", String.valueOf(rt.freeMemory() / MB));
        KeyValuePair maxMemory = new KeyValuePair("max memory [MB]", String.valueOf(rt.maxMemory() / MB));
        KeyValuePair usedMemory = new KeyValuePair("used memory [MB]", String.valueOf((rt.totalMemory() - rt.freeMemory()) / MB));

        List<KeyValuePair> data = new LinkedList();
        data.add(totalMemory);
        data.add(freeMemory);
        data.add(maxMemory);
        data.add(usedMemory);

        VelocityContext ctx = new VelocityContext();
        ctx.put("pairs", data);
        ctx.put("path", path);
        StringWriter sw = new StringWriter();
        this.ve.evaluate(ctx, sw, "info", this.infoTemplate);
        this.cfg.writeResponse(req, resp, sw.toString());
    }

    @Data
    public static class KeyValuePair {

        protected String key;
        protected String value;

        public KeyValuePair(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
