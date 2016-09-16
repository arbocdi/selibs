package net.sf.selibs.http.nio;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import net.sf.selibs.http.constants.HNames;
import net.sf.selibs.http.constants.HValues;
import net.sf.selibs.http.constants.ParserConstants;
import net.sf.selibs.http.io.HSerializer;

/**
 *
 * @author ashevelev
 */
public class HeaderState implements ParserState {

    public static final String NO_CUNK_NO_LEN = "No Content-Length or Transfer-Encoding: chunked found, cant parse message";

    protected ByteArrayOutputStream headersRaw = new ByteArrayOutputStream();
    protected ByteStack headerBS = new ByteStack(ParserConstants.END_HEADER.length);

    @Override
    public void parseByte(ParserContext ctx, byte data) throws ParserException {
        headerBS.addByte(data);
        headersRaw.write(data);
        if (headerBS.equalsArray(ParserConstants.END_HEADER)) {
            this.separatorReached(ctx);
        }
    }

    protected void separatorReached(ParserContext ctx) throws ParserException {
        try {
            ctx.message = HSerializer.fromString(new String(this.headersRaw.toByteArray(), "UTF-8"));
        } catch (Exception ex) {
            throw new ParserException(ex);
        }
        String cLenStr = ctx.message.getHeaderValue(HNames.CONTENT_LENGTH);
        if (cLenStr != null) {
            int cLen = Integer.parseInt(cLenStr);
            if (cLen == 0) {
                ctx.finished = true;
            }
            ctx.setState(new ContentLengthState(cLen));
            return;
        }
        String transferEncoding = ctx.message.getHeaderValue(HNames.TRANSFER_ENCODING);
        if (transferEncoding != null && transferEncoding.contains(HValues.CHUNKED)) {
            ctx.setState(new ChunkedState());
            return;
        }
        ctx.finished = true;
    }

}
