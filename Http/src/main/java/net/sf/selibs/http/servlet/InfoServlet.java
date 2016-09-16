package net.sf.selibs.http.servlet;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.constants.HCodes;
import net.sf.selibs.http.constants.HNames;
import net.sf.selibs.http.constants.HValues;
import net.sf.selibs.http.constants.HVersions;
import net.sf.selibs.http.html.HTMLPrinter;
import net.sf.selibs.http.html.HTMLTags;
import org.simpleframework.xml.Root;
@Root
public class InfoServlet implements HServlet {

    protected HTMLPrinter printer;

    public InfoServlet() {
        printer = new HTMLPrinter();
        printer.setFields(Link.class.getDeclaredFields());
        printer.setTitle("Available pages");
    }

    @Override
    public HMessage handle(HMessage i) throws Exception {
        Map<String, HServlet> servlets = (Map<String, HServlet>) i.attachements.get(HServletMap.SERVLETS);
        String html = this.printer.generateDocument(getLinks(servlets));
        HMessage response = HMessage.createResponse(HVersions.V11,
                HCodes.OK,
                "OK",
                html.getBytes("UTF-8"));
        response.addHeader(HNames.CONTENT_TYPE, HValues.TEXT_HTML);
        return response;
    }

    public static List<Link> getLinks(Map<String, HServlet> servlets) {
        List<Link> links = new LinkedList();
        for (Map.Entry<String, HServlet> entry : servlets.entrySet()) {
            Link link = new Link();
            link.link = String.format(HTMLTags.HYPERLINK,entry.getKey(),entry.getValue().getClass().getName());
            links.add(link);
        }
        return links;
    }

    public static class Link {

        public String link;
    }

}
