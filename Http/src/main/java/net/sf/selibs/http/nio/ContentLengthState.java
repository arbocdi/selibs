package net.sf.selibs.http.nio;

import java.io.ByteArrayOutputStream;

public class ContentLengthState implements ParserState {

    //config========
    protected int contentLength;
    //work==========
    protected ByteArrayOutputStream bodyRaw = new ByteArrayOutputStream();
    protected int bytesParsed;

    public ContentLengthState(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public void parseByte(ParserContext ctx, byte data) throws ParserException {
        bodyRaw.write(data);
        bytesParsed++;
        if(bytesParsed==this.contentLength){
            ctx.message.payload = bodyRaw.toByteArray();
            ctx.finished=true;
        }
    }
}
