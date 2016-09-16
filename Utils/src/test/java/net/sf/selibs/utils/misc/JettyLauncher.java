package net.sf.selibs.utils.misc;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.BasicConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

@Log4j
public class JettyLauncher {

    static {
        BasicConfigurator.configure();
        final Server server = new Server(7070);
        try {
            ServletContextHandler handler = new ServletContextHandler(server, "/");
            handler.addServlet(EchoServlet.class, "/echo");
            server.setHandler(handler);
            server.start();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        server.stop();
                        server.join();
                    } catch (Exception ex) {
                        log.warn("Cant stop jetty server", ex);
                    }
                }
            });
        } catch (Exception ex) {
            log.warn("Cant start jetty server", ex);
            try {
                server.stop();
                server.join();
            } catch (Exception ex1) {
            }
        }
    }

    public static class EchoServlet extends HttpServlet {

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setCharacterEncoding("utf-8");
            resp.setContentType("text/plain; charset=utf-8");
            String line = req.getReader().readLine();
            resp.getWriter().print(line);
        }

    }
}
