package net.sf.selibs.velocity_ui.jetty;

import java.util.LinkedList;
import java.util.List;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import net.sf.selibs.utils.inject.Injector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class ServerConfig {

    @Element
    public ServletContextHandlerConfig handlerConfig;
    @Element
    public ConnectorConfig connectorConfig;

    @Root
    public static class ServletContextHandlerConfig {

        @Element
        public String path;
        @ElementList
        public List<ServletConfig> servlets = new LinkedList();

        public void addContextHandler(Server srv, Injector i) throws IllegalArgumentException, IllegalAccessException {
            ServletContextHandler context = new ServletContextHandler(
                    ServletContextHandler.SESSIONS);
            context.setContextPath(this.path);
            for (ServletConfig cfg : servlets) {
                cfg.addServlet(context, i);
            }
            srv.setHandler(context);
        }
    }

    @Root
    public static class ServletConfig {

        @Element
        public String path;
        @Element(required = false)
        public HttpServlet servlet;
        @Element(required = false)
        public String servletClass;
        @Element
        public boolean async;
        @Element
        public int initOrder;

        public ServletConfig() {

        }

        public ServletConfig(String path, String servletClass) {
            this.path = path;
            this.servletClass = servletClass;
        }

        public ServletConfig(String path, HttpServlet servlet) {
            this.path = path;
            this.servlet = servlet;
        }

        public void addServlet(ServletContextHandler context, Injector i) throws IllegalArgumentException, IllegalAccessException {
            if (this.servletClass != null) {
                context.addServlet(this.servletClass, path);
            } else {
                ServletHolder holder = new ServletHolder();
                i.injectIntoGraph(servlet);
                holder.setServlet(servlet);
                holder.setInitOrder(initOrder);
                holder.setAsyncSupported(async);
                context.addServlet(holder, path);
            }
        }
    }

    public static class ConnectorConfig {

        @Element(required = false)
        public String host;
        @Element
        public int port;
        @Element(required = false)
        public Long timeout;

        public void addConnector(Server server) {
            ServerConnector connector = new ServerConnector(server);
            connector.setPort(port);
            connector.setHost(host);
            if (this.timeout != null) {
                connector.setIdleTimeout(this.timeout);
            }
            server.addConnector(connector);
        }
    }

    public Server createServer(Injector i) throws IllegalArgumentException, IllegalAccessException {
        Server srv = new Server();
        
        this.connectorConfig.addConnector(srv);
        this.handlerConfig.addContextHandler(srv, i);
        return srv;
    }

    public static ServerConfig generateDefault() {
        ServerConfig serverCfg = new ServerConfig();
        //connector
        ConnectorConfig connectorCfg = new ConnectorConfig();
        connectorCfg.host = "127.0.0.1";
        connectorCfg.port = 2121;
        connectorCfg.timeout = 30000L;
        serverCfg.connectorConfig = connectorCfg;
        //hello servlet
        ServletConfig helloServletCfg = new ServletConfig();
        helloServletCfg.path = "/hello";
        helloServletCfg.servlet = new HelloServlet();
        //ServletContextHandlerConfig
        ServletContextHandlerConfig handlerCfg = new ServletContextHandlerConfig();
        handlerCfg.path = "/app";
        handlerCfg.servlets = new LinkedList();
        handlerCfg.servlets.add(helloServletCfg);
        serverCfg.handlerConfig = handlerCfg;

        return serverCfg;
    }

}
