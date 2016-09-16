package net.sf.selibs.http;

//HTTP/v.v CODE COMMENT
public class ResponseLine implements InitialLine {

    public String version;
    public int code;
    public String comment;

    public static ResponseLine fromString(String str) {
        ResponseLine line = new ResponseLine();
        String[] data = str.split(" ");
        line.version = data[0].trim();
        line.code = Integer.parseInt(data[1]);
        StringBuilder comment = new StringBuilder();
        for (int i = 2; i < data.length; i++) {
            comment.append(data[i]);
            if (i != (data.length - 1)) {
                comment.append(" ");
            }
        }
        line.comment = comment.toString();
        return line;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(version);
        sb.append(" ");
        sb.append(code);
        sb.append(" ");
        sb.append(comment);
        return sb.toString();
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(String hVersion) {
        this.version = hVersion;
    }
}
