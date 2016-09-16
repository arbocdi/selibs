package net.sf.selibs.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.Getter;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.io.HSerializer;
import net.sf.selibs.http.nio.ParserException;
import net.sf.selibs.utils.io.streams.OOSWrapper;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class OpenClient {

    //config========
    @Element
    public TCPConfig cfg;
    @Element
    public boolean disableCertificatevalidation;
    @Element
    public boolean ssl;        
    //work==========
    protected SSLSocketFactory socketFactory;
    @Getter
    protected InputStream from;
    @Getter
    protected OutputStream to;
    @Getter
    protected Socket socket;

    public void openConnection() throws IOException {
        if (this.disableCertificatevalidation) {
            this.disableCertificateValidation();
        }
        if (ssl) {
            this.createCryptedSocket(cfg.ip, cfg.port, cfg.timeout);
        } else {
            this.createSocket(cfg.ip, cfg.port, cfg.timeout);
        }
        this.openStreams();
    }

    public HMessage makeHttpRequest(HMessage request) throws IOException, ParserException {
        HSerializer.toStream(to, request);
        return HSerializer.fromStream(from);
    }

    public void createSocket(String ip, int port, int timeout) throws IOException {
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(ip, port), timeout);
        this.socket.setSoTimeout(timeout);
    }

    public void createCryptedSocket(String ip, int port, int timeout) throws IOException {
        if (this.socketFactory == null) {
            this.socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        }
        this.socket = socketFactory.createSocket(ip, port);
        this.socket.setSoTimeout(timeout);
    }

    public void openStreams() throws IOException {
        from = socket.getInputStream();
        to = socket.getOutputStream();
    }

    public void openBufferedStreams() throws IOException {
        from = new BufferedInputStream(from);
        to = new BufferedOutputStream(to);
    }

    public void openObjectStreams() throws IOException {
        to = new OOSWrapper(to);
        from = new ObjectInputStream(from);
    }

    public void disableCertificateValidation() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

        // Ignore differences between given hostname and certificate hostname
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            this.socketFactory = sc.getSocketFactory();
            //HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            //HttpsURLConnection.setDefaultHostnameVerifier(hv);
        } catch (Exception e) {
        }
    }

    public void closeConnection() {
        if (this.from != null) {
            try {
                from.close();
            } catch (Exception ex) {

            }
        }
        if (this.to != null) {
            try {
                to.close();
            } catch (Exception ex) {

            }
        }
        if (this.socket != null) {
            try {
                socket.close();
            } catch (Exception ex) {

            }
        }
    }

}
