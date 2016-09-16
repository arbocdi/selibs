package net.sf.selibs.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.selibs.http.constants.HNames;
import net.sf.selibs.http.constants.HValues;
import net.sf.selibs.http.constants.HVersions;
import net.sf.selibs.http.io.ChunkedInputStream;
import net.sf.selibs.http.io.HSerializer;

public class HMessage implements Serializable {

    protected static Pattern p = Pattern.compile("charset=(.*)");

    public InitialLine line;
    public LinkedHashMap<HName, HHeader> headers = new LinkedHashMap();
    public byte[] payload;
    public transient LinkedHashMap<String, Object> attachements = new LinkedHashMap();

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.attachements = new LinkedHashMap();
    }

    public HHeader addHeader(HHeader header) {
        return this.headers.put(header.name, header);
    }

    public HHeader addHeader(String name, String value) {
        HHeader header = new HHeader();
        header.name = new HName(name);
        header.value = value;
        return this.headers.put(header.name, header);
    }

    public String getHeaderValue(String hName) {
        HName name = new HName(hName);
        HHeader header = this.headers.get(name);
        if (header != null) {
            return header.value;
        } else {
            return null;
        }
    }

    public boolean getConnectionClose() {
        String conValue = this.getHeaderValue(HNames.CONNECTION);
        return conValue != null && conValue.equalsIgnoreCase(HValues.CLOSE);
    }

    @Override
    public String toString() {
        try {
            return new String(HSerializer.toByteArray(this), "UTF-8");
        } catch (IOException ex) {

        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HMessage other = (HMessage) obj;
        return this.toString().equals(other.toString());
    }

    public static HMessage createRequest(String method, String version, URI path, String host, byte[] payload) {
        HMessage request = new HMessage();
        RequestLine rLine = new RequestLine();
        rLine.method = method;
        rLine.url = path;
        rLine.version = version;
        request.line = rLine;
        request.addHeader(HNames.HOST, host);
        request.payload = payload;
        if (payload != null) {
            request.addHeader(HNames.CONTENT_LENGTH, String.valueOf(payload.length));
        }
        return request;
    }

    public static HMessage createRequest(String method, URI path, String host) {
        return HMessage.createRequest(method, HVersions.V11, path, host, null);
    }

    public static HMessage createResponse(String version, int code, String descr, byte[] payload) {
        HMessage response = new HMessage();
        ResponseLine rLine = new ResponseLine();
        rLine.version = version;
        rLine.code = code;
        rLine.comment = descr;
        response.line = rLine;
        if (payload != null) {
            HHeader cLen = new HHeader();
            cLen.name = new HName(HNames.CONTENT_LENGTH);
            cLen.value = String.valueOf(payload.length);
            response.addHeader(cLen);
        }
        response.payload = payload;
        return response;
    }

    public static HMessage createResponse(int code, String descr) {
        return HMessage.createResponse(HVersions.V11, code, descr, descr.getBytes());
    }

    public String getEncoding() {
        synchronized (this.getClass()) {
            String contentType = this.getHeaderValue(HNames.CONTENT_TYPE);
            if (contentType == null) {
                return "UTF-8";
            }
            Matcher m = p.matcher(contentType);
            if (!m.find()) {
                return "UTF-8";
            }
            return m.group(1).toUpperCase();
        }
    }

    public Reader getReader() throws IOException {
        String encoding = this.getEncoding();
        Reader reader;
        //if chunked encoding
        if (getHeaderValue(HNames.TRANSFER_ENCODING) != null
                && getHeaderValue(HNames.TRANSFER_ENCODING).contains(HValues.CHUNKED)) {
            reader = new InputStreamReader(new ChunkedInputStream(new ByteArrayInputStream(payload)), encoding);

        } else {
            reader = new InputStreamReader(new ByteArrayInputStream(payload), encoding);
        }
        return reader;
    }

}
