package net.sf.selibs.velocity_ui.servlets;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.utils.io.FileUtils;
import net.sf.selibs.velocity_ui.VelocityUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class HNBConfig {

    //vonfig=============
    @Getter
    @Setter
    @Element
    protected Style style = Style.getGreyStyle();
    @Getter
    @Setter
    @Element
    protected String header;
    @Getter
    @Setter
    @Element
    protected String title;
    @Getter
    @Setter
    @ElementList
    protected List<HLink> links = new LinkedList();
    //work===============
    protected VelocityEngine ve;
    protected String styleTemplate;
    protected String htmlTemplate;

    public HNBConfig() throws Exception {
        ve = VelocityUtils.createEngine();
        this.styleTemplate = new String(FileUtils.readFromCP(HNBConfig.class, "hnb.css"), "UTF-8");
        this.htmlTemplate = new String(FileUtils.readFromCP(HNBConfig.class, "hnb.html"), "UTF-8");
    }

    public String getStyleStr() {
        VelocityContext ctx = new VelocityContext();
        ctx.put("style", this.style);
        StringWriter sw = new StringWriter();
        ve.evaluate(ctx, sw, HNBConfig.class.getName(), styleTemplate);
        return sw.toString();
    }

    public static List<HLink> cloneLinks(List<HLink> links, String reqPath) {
        List<HLink> out = new LinkedList();
        for (HLink link : links) {
            HLink clone = link.clone();
            if (clone.getPath().equals(reqPath)) {
                clone.setActive(true);
            }
            out.add(clone);
        }
        return out;
    }

    public void writeResponse(HttpServletRequest req, HttpServletResponse resp, String body) throws IOException {
        VelocityContext ctx = new VelocityContext();
        String reqPath = req.getServletPath();
        //make path relative /name -> name
        if (reqPath.length() > 1) {
            reqPath = reqPath.substring(1, reqPath.length());
        }
        ctx.put("links", cloneLinks(links, reqPath));
        ctx.put("style", this.getStyleStr());
        ctx.put("title", this.title);
        ctx.put("header", this.header);
        ctx.put("body", body);
        resp.setContentType("text/html;charset=utf-8");
        resp.setCharacterEncoding("utf-8");
        ve.evaluate(ctx, resp.getWriter(), "hnb", this.htmlTemplate);
        StringWriter sw = new StringWriter();
    }

}
