package net.sf.selibs.http.nio;

import java.io.ByteArrayOutputStream;
import net.sf.selibs.http.constants.ParserConstants;

/**
 *
 * @author ashevelev
 */
public class ChunkedState implements ParserState {

    //work==========
    protected ByteArrayOutputStream bodyRaw = new ByteArrayOutputStream();
    protected ByteStack bs = new ByteStack(ParserConstants.END_CHUNKS.length);

    @Override
    public void parseByte(ParserContext ctx, byte data) throws ParserException {
        bs.addByte(data);
        bodyRaw.write(data);
        if (bs.equalsArray(ParserConstants.END_CHUNKS)) {
            ctx.message.payload = bodyRaw.toByteArray();
            ctx.finished=true;
        }
    }

}
