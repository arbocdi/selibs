package net.sf.selibs.utils.misc;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import org.apache.log4j.BasicConfigurator;

public class EmbeddedHttp {

    private static HttpServer server;

    public static void createSingleton() throws IOException {
        if (server == null) {
            BasicConfigurator.configure();
            server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/test", new MyHandler());
            server.setExecutor(null); // creates a default executor
            server.start();
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run(){
                    server.stop(10);
                }
            });
        }
    }

    static class MyHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the response";
            t.sendResponseHeaders(200, response.getBytes("UTF-8").length);
            OutputStream os = null;
            try {
                os = t.getResponseBody();
                os.write(response.getBytes());
            } finally {
                UHelper.close(os);
            }
        }
    }
}
