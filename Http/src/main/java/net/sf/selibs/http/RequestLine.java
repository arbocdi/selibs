package net.sf.selibs.http;

//METHOD URL HTTP/V.V
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class RequestLine implements InitialLine {
    
    public String method;
    public URI url;
    public String version;

    public static RequestLine fromString(String str) throws URISyntaxException {
        RequestLine line = new RequestLine();
        String[] parts = str.split(" ");
        line.method = parts[0].trim();
        line.url = new URI(parts[1]);
        line.version = parts[2].trim();
        return line;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.method);
        sb.append(" ");
        sb.append(this.url.toASCIIString());
        sb.append(" ");
        sb.append(this.version);
        return sb.toString();
    }

    public Query parseQuery() {
        if(this.url.getQuery()==null){
            return null;
        }
        return Query.fromString(this.url.getQuery());
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(String hVersion) {
        this.version = hVersion;
    }

    public static class Query {

        public Map<String, String> data = new HashMap();

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : this.data.entrySet()) {
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue());
                sb.append("&");
            }
            if (sb.charAt(sb.length() - 1) == '&') {
                sb.deleteCharAt(sb.length() - 1);
            }
            return sb.toString();
        }

        public static Query fromString(String str) {
            Query q = new Query();
            String[] pairs = str.split("&");
            for (String pair : pairs) {
                String[] nameValue = pair.split("=");
                q.data.put(nameValue[0], nameValue[1]);
            }
            return q;
        }
    }

}
