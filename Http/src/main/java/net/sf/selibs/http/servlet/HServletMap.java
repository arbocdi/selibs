package net.sf.selibs.http.servlet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.RequestLine;
import net.sf.selibs.http.constants.HCodes;
import net.sf.selibs.http.constants.HVersions;
import net.sf.selibs.utils.chain.HException;
import net.sf.selibs.utils.graph.Node;
import net.sf.selibs.utils.misc.UHelper;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

/**
 * At the end of a pattern, /* matches any sequence of characters from that
 * point forward. The pattern *.extension matches any file name ending with
 * extension. No other wildcards are supported, and an asterisk at any other
 * position in the pattern is not a wildcard.
 *
 * The servlet container uses a straightforward matching procedure to determine
 * the best match. The matching procedure has four simple rules. First, the
 * container prefers an exact path match over a wildcard path match. Second, the
 * container prefers to match the longest pattern. Third, the container prefers
 * path matches over filetype matches.Finally, the pattern
 * 
 * servlet mapping: /test/servlet/*
 * will match path /test/servlet
 * will match path /test/servlet/
 * will match path /test/servlet/abc
 * <url-pattern>/</url-pattern> always matches any request that no other pattern
 * matches/
 *
 */
@Root
public class HServletMap implements HServlet {

    public static final String SERVLETS = "servlets";

    @ElementMap
    @Setter
    @Getter
    @Node
    protected Map<String, HServlet> servlets = new HashMap();
    @Element(required = false)
    @Setter
    @Getter
    @Node
    protected HServlet defaultServlet;

    public HServlet addServlet(String path, HServlet servlet) {
        return this.servlets.put(path, servlet);
    }

    public HServlet getServlet(String path) {
        if (path.equals("/")) {
            return this.defaultServlet;
        }
        //exact path
        HServlet servlet = this.servlets.get(path);
        if (servlet != null) {
            return servlet;
        }
        //wildcard longest match
        String longestMatchedPath = null;
        f1:
        for (String p : this.servlets.keySet()) {
            if (p.matches("^.*/\\*$")) {
                String prefix = p.split("\\*")[0];
                String prefixExact = prefix.substring(0, prefix.length() - 1);
                if (path.equals(prefixExact)) {
                    longestMatchedPath = p;
                    break f1;
                }
                if (path.startsWith(prefix)) {
                    if (longestMatchedPath == null) {
                        longestMatchedPath = p;
                    } else {
                        if (p.length() > longestMatchedPath.length()) {
                            longestMatchedPath = p;
                        }
                    }
                }
            }
        }
        servlet = this.servlets.get(longestMatchedPath);
        if (servlet != null) {
            return servlet;
        }
        //default servlet
        return this.defaultServlet;
    }

    @Override
    public HMessage handle(HMessage request) throws HException {
        request.attachements.put(SERVLETS, this.servlets);
        try {
            String path = ((RequestLine) request.line).url.getPath();
            HServlet servlet = this.getServlet(path);
            if (servlet != null) {
                return servlet.handle(request);
            } else {
                return HMessage.createResponse(HCodes.NOT_FOUND, "NOT FOUND");
            }
        } catch (Exception ex) {
            try {
                return HMessage.createResponse(HVersions.V11, 500, "Internal Error", UHelper.getStackTrace(ex).getBytes("UTF-8"));

            } catch (Exception exx) {
                throw new HException(exx);
            }
        }
    }

    public HServletMap() {

    }

}
