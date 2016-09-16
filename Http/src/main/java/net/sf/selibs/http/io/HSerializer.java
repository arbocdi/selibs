package net.sf.selibs.http.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import net.sf.selibs.http.HHeader;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.RequestLine;
import net.sf.selibs.http.ResponseLine;
import net.sf.selibs.http.Utils;
import net.sf.selibs.http.nio.ParserContext;
import net.sf.selibs.http.nio.ParserException;
import net.sf.selibs.utils.io.FileUtils;

public class HSerializer {

    public static final byte[] LINE_SEPARATOR = {'\r', '\n'};

    public static void toStream(OutputStream out, HMessage message) throws IOException {
        out.write(message.line.toString().getBytes("UTF-8"));
        out.write(LINE_SEPARATOR);
        for (HHeader header : message.headers.values()) {
            out.write(header.toStgring().getBytes("UTF-8"));
            out.write(LINE_SEPARATOR);
        }
        out.write(LINE_SEPARATOR);
        if (message.payload != null) {
            out.write(message.payload);
        }
        out.flush();
    }

    public static byte[] toByteArray(HMessage message) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        HSerializer.toStream(bout, message);
        return bout.toByteArray();
    }

    public static HMessage fromString(String str) throws URISyntaxException {
        HMessage message = new HMessage();
        String[] data = str.split("\r\n");
        //make first line
        if (data[0].startsWith("HTTP")) {
            message.line = ResponseLine.fromString(data[0]);
        } else {
            message.line = RequestLine.fromString(data[0]);
        }
        //make headers
        List<HHeader> headers = new ArrayList();
        for (int lnum = 1; lnum < data.length; lnum++) {
            String line = data[lnum];

            if (line.trim().isEmpty()) {
                break;
            }
            if (line.startsWith(" ") || line.startsWith("\t")) {
                HHeader header = headers.get(headers.size() - 1);
                header.value += Utils.removeWhiteSpaces(line);
            } else {
                HHeader header = HHeader.fromString(line);
                message.headers.put(header.name, header);
                headers.add(header);
            }
        }
        return message;
    }

    public static HMessage fromStream(InputStream in) throws IOException, ParserException {
        ParserContext ctx = new ParserContext();
        while (true) {
            int bt = in.read();
            if (bt == -1) {
                throw new ParserException("Stream had ended before message was read "+ctx.getRawDataAsString());
            }
            if (ctx.parseByte((byte) bt)) {
                break;
            }
        }
        return ctx.getMessage();
    }

    public static HMessage fromByteArray(byte[] data) throws ParserException {
        ParserContext ctx = new ParserContext();
        ctx.parseBytes(data);
        return ctx.getMessage();
    }

    public static HMessage fromFile(String fName) throws ParserException, IOException {
        return HSerializer.fromByteArray(FileUtils.readToByteArray(fName));
    }

}
