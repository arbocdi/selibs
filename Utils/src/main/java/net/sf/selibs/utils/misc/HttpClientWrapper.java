package net.sf.selibs.utils.misc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.utils.service.Service;
import net.sf.selibs.utils.service.ServiceException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class HttpClientWrapper extends Service {

    //config===========
    @Element
    @Setter
    @Getter
    /**
     * Timeout in seconds
     */
    protected int idleTimeout = 60;
    @Element
    @Setter
    @Getter
    protected int maxConnections = 100;
    @Element
    @Setter
    @Getter
    protected int maxConnectionsPerRoute = 20;

    //work=============
    @Getter
    protected PoolingHttpClientConnectionManager cm;
    @Getter
    CloseableHttpClient httpClient;

    public HttpClientWrapper() {
        this.sleepInterval = 10000;
    }

    public HttpClientWrapper(@Element(name = "sleepInterval") int sleepInterval) {
        this.sleepInterval = sleepInterval;
    }

    /**
     * When an HttpClient instance is no longer needed and is about to go out of
     * scope it is important to shut down its connection manager to ensure that
     * all connections kept alive by the manager get closed and system resources
     * allocated by those connections are released. A connection can be closed
     * gracefully (an attempt to flush the output buffer prior to closing is
     * made), or forcefully, by calling the shutdown method (the output buffer
     * is not flushed). To properly close connections we need to do all of the
     * following:
     *
     * consume and close the response (if closeable)
     *
     * close the client
     *
     * close and shut down the connection manager
     *
     */
    @Override
    public void stop() {
        super.stop();
        UHelper.close(httpClient);
        UHelper.close(cm);
        cm.shutdown();
    }

    @Override
    public void start() throws ServiceException {
//        PoolingHttpClientConnectionManager is a more complex implementation that 
//        manages a pool of client connections and is able to service connection requests 
//        from multiple execution threads. Connections are pooled on a per route basis. 
//        A request for a route for which the manager already has a persistent connection 
//        available in the pool will be serviced by leasing a connection from the pool 
//        rather than creating a brand new connection.
        cm = new PoolingHttpClientConnectionManager();
        // Increase max total connection to 200
        cm.setMaxTotal(this.maxConnections);
        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(this.maxConnectionsPerRoute);
//        HttpClient implementations are expected to be thread safe. 
//        It is recommended that the same instance of this class is reused 
//        for multiple request executions.
        httpClient = HttpClients.custom()
                .setConnectionManager(cm).setRetryHandler(new SafeRetryHandler())
                .build();
        super.start();
    }

    public Object makeRequest(ResponseHandler handler, HttpUriRequest request) throws IOException {
        return this.httpClient.execute(request, handler);
    }

    public String makeStringRequest(HttpUriRequest request) throws IOException {
        return this.httpClient.execute(request, new StringResponseHandler());
    }

    @Override
    /**
     * Connections check:This is a general limitation of the blocking I/O in Java. There is simply
     * no way of finding out whether or not the opposite endpoint has closed
     * connection other than by attempting to read from the socket. Apache
     * HttpClient works this problem around by employing the so stale connection
     * check which is essentially a very brief read operation.
     */
    protected void doStuff() {

        // Close expired connections
        cm.closeExpiredConnections();
        // Optionally, close connections
        // that have been idle longer than 30 sec
        cm.closeIdleConnections(idleTimeout, TimeUnit.SECONDS);
    }

    public static class StringResponseHandler implements ResponseHandler<String> {

        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            if (statusLine.getStatusCode() >= 300) {
                throw new HttpResponseException(
                        statusLine.getStatusCode(),
                        statusLine.getReasonPhrase());
            }
            if (entity == null) {
                throw new ClientProtocolException("Response contains no content");
            }
            ContentType contentType = ContentType.getOrDefault(entity);
            Charset charset = contentType.getCharset();
            return EntityUtils.toString(entity, charset);
        }

    }

    public static class SafeRetryHandler implements HttpRequestRetryHandler {

        @Override
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            return false;
        }

    }

}
