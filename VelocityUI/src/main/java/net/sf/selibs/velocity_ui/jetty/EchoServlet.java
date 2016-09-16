package net.sf.selibs.velocity_ui.jetty;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class EchoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain; charset=utf-8");
        resp.getWriter().println("EchoServlet response "+req.getPathInfo()+":"+req.getQueryString());
    }
    
}
