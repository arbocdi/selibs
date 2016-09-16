package net.sf.selibs.velocity_ui.servlets;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.simpleframework.xml.Root;

@Root
public class NotFoundServlet extends HttpServlet {

    @Setter
    @Getter
    @Inject
    protected HNBConfig cfg;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.cfg.writeResponse(req, resp, "<p>Not Found</p>");
    }

}
