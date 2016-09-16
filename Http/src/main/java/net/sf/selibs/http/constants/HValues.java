package net.sf.selibs.http.constants;

public interface HValues {

    public static final String CHUNKED = "chunked";
    public static final String CLOSE = "close";
    public static final String GZIP = "gzip";
    public static final String TEXT = "text";
    public static final String TEXT_HTML = "text/html";
    public static final String KEEP_ALIVE = "keep-alive";
    public static final String UTF8 = "UTF-8";
    public static final String CHARSET = "charset";
    /**
     * Content-Type: application/x-javascript; charset=UTF-8
     */
    public static final String DATA_WITH_ENCODING = "%s; charset=%s";
   
}
