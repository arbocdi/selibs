package net.sf.selibs.http.constants;

public interface HNames {
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";//chunked
    public static final String CONNECTION = "Connection";//close | keep-alive
    public static final String ACCEPT_ENCODING = "Accept-Encoding"; // gzip, deflate
    public static final String CONTENT_TYPE = "Content-Type";//text | text/html; charset=iso-8859-1 |message/http | application/x-www-form-urlencoded |multipart/form-data
    public static final String HOST = "Host";
    public static final String ACCEPT = "Accept";//image/gif, image/jpeg
    public static final String ACCEPT_LANGUAGE = "Accept-Language";//en-US,en
    public static final String ACCEPT_CHARSET="Accept-Charset";
    public static final String AUTHORIZATION = "Authorization";
    public static final String COOKIE = "Cookie";
    public static final String CONTENT_ENCODING = "Content-Encoding";//gzip
}
