package net.sf.selibs.http.html;

public class HTMLTags {

    /**
     * строка таблицы
     */
    public static final String TR = "tr";
    /**
     * столбец-заголовок таблицы
     */
    public static final String TH = "th";
    /**
     * столбец-данные таблицы
     */
    public static final String TD = "td";

    public static final String TABLE = "table";
    /**
     * Новая строка.
     */
    public static final String BR = "br";

    public static final String HTML = "html";
    public static final String HEAD = "head";
    public static final String BODY = "body";
    public static final String TITLE = "title";
    public static final String DOC = "<html>\n"
            + "<head>\n"
            + "<title>%s</title>\n"
            + "<meta charset=\"UTF-8\">\n"
            + "<style>"
            + "#style#"
            + "</style>"
            + "</head>\n"
            + "<body>\n%s"
            + "</body>\n"
            + "</html>\n";
    // <a href="http://www.w3schools.com">Visit W3Schools.com!</a> 
    public static final String HYPERLINK = " <a href=\"%s\">%s</a> ";

    public static String getOpen(String tag) {
        return String.format("<%s>", tag);
    }

    public static String getClose(String tag) {
        return String.format("</%s>\n", tag);
    }

    public static String getNoBody(String tag) {
        return String.format("<%s/>\n", tag);
    }

    public static String getWithBody(String tag, String body) {
        return String.format("%s%s%s", HTMLTags.getOpen(tag), body, HTMLTags.getClose(tag));
    }

}
