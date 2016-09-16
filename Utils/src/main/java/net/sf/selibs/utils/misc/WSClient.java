package net.sf.selibs.utils.misc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import net.sf.selibs.utils.chain.Handler;
import net.sf.selibs.utils.io.IOUtils;

public class WSClient {

    public static byte[] makeRequest(String urlStr, String method) throws MalformedURLException, IOException {
        return makeRequest(urlStr, method, null, null,Proxy.NO_PROXY);
    }

    public static byte[] makeRequest(String urlStr, String method, byte[] data, String contentType,Proxy proxy) throws MalformedURLException, IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setRequestMethod(method);
            if (data != null) {
                connection.setRequestProperty("Content-Type", contentType);
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", String.valueOf(data.length));
                connection.setInstanceFollowRedirects(false);
                connection.setUseCaches(false);
                connection.setDoOutput(true);
                out = connection.getOutputStream();
                out.write(data);
            }
            in = connection.getInputStream();
            return IOUtils.readFully(in);
        } finally {
            UHelper.close(in);
            UHelper.close(out);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static Object makeRequest(String urlStr, String method, Handler<InputStream, Object> handler,Proxy proxy) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection connection = null;
        InputStream in = null;
        try {
            connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setRequestMethod(method);

            in = connection.getInputStream();
            return handler.handle(in);
        } finally {
            UHelper.close(in);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
