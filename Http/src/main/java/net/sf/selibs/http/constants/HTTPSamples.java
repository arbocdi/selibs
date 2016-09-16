package net.sf.selibs.http.constants;

public interface HTTPSamples {

    public static final String NO_BODY = "GET /test.xml HTTP/1.1\r\n"
            + "Host: localhost:8080\r\n\r\n";

    public static final String LENGTH_BODY = "GET /test.xml HTTP/1.1\r\n"
            + "Content-Length: 3\r\n\r\n"
            + "abc";
    public static final String CUNK_BODY = "GET /test.xml HTTP/1.1\r\n"
            + "Transfer-Encoding: chunked\r\n\r\n"
            + "3\r\n"
            + "abc\r\n"
            + "0\r\n\r\n";

    public static String lengthBogy = "<html>\r\n"
            + "<head>\r\n"
            + "  <title>An Example Page</title>\r\n"
            + "</head>\r\n"
            + "<body>\r\n"
            + "  Hello World, this is a very simple HTML document.\r\n"
            + "</body>\r\n"
            + "</html>  ";

    public static String message = "HTTP/1.1 200 OK\r\n"
            + "Date: Mon, 23 May 2005 22:38:34 GMT\r\n"
            + "Server: Apache/1.3.3.7 (Unix) (Red-Hat/Linux)\r\n"
            + "Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\r\n"
            + "Content-Type: text/html; charset=UTF-8\r\n"
            + "Content-Length: " + lengthBogy.length() + "\r\n"
            + "Connection: keep-alive\r\n\r\n"
            + lengthBogy;
    public static String chunkedMessage = "HTTP/1.1 200 OK\r\n"
            + "Server: nginx/1.0.4\r\n"
            + "Date: Thu, 06 Oct 2011 16:14:01 GMT\r\n"
            + "Content-Type: text/html\r\n"
            + "Transfer-Encoding: chunked\r\n"
            + "Accept-Encoding: gzip,text\r\n"
            + "Connection: keep-alive\r\n"
            + "Vary: Accept-Encoding\r\n"
            + "X-Powered-By: PHP/5.3.623\r\n\r\n"
            + "23\r\n"
            + "This is the data in the first chunk\r\n"
            + "1A\r\n"
            + "and this is the second one\r\n"
            + "3\r\n"
            + "con\r\n"
            + "8\r\n"
            + "sequence\r\n"
            + "0\r\n\r\n";
    String OK_HEADER = "HTTP/1.0 200 OK\r\n"
            + "Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n\r\n";
    String OK_MULTILINE_HEADER = "HTTP/1.0 200 OK\r\n"
            + "Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n"
            + "Content-Type: text/html\r\n"
            + "Content-Length: 1354\r\n"
            + "Header: value1,\r\n"
            + "        value2\r\n\r\n";
    String ZERO_LEN_REPLY = "HTTP/1.1 400 Bad Request\r\n"
            + "Server: GlassFish Server Open Source Edition  4.1\r\n"
            + "X-Powered-By: Servlet/3.1 JSP/2.3 (GlassFish Server Open Source Edition  4.1  Java/Oracle Corporation/1.8)\r\n"
            + "Date: Wed, 07 Oct 2015 12:01:21 GMT\r\n"
            + "Connection: close\r\n"
            + "Content-Length: 0\r\n\r\n";

}
