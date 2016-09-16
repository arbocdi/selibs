package net.sf.selibs.http.nio;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import lombok.Getter;
import net.sf.selibs.http.HMessage;

public class ParserContext {

    //current state====        
    protected ParserState state;
    //work=============
    @Getter
    protected HMessage message = new HMessage();
    @Getter
    protected ByteArrayOutputStream rawData = new ByteArrayOutputStream();
    protected boolean finished;

    public ParserContext() {
        this.setState(new HeaderState());
    }

    public boolean parseBytes(byte[] data) throws ParserException {
        boolean result = false;
        for (byte bt : data) {
            result = this.parseByte(bt);
        }
        return result;
    }

    /**
     *
     * @param data
     * @return true если парсинг сообщения закончен и не требуется большего
     * числа данных
     * @throws ParserException
     */
    public boolean parseByte(byte data) throws ParserException {
        if (finished) {
            try {
                throw new ParserException("No more data required to parse HTTPMessage!\r\n" + this.getRawDataAsString());
            } catch (Exception ex) {
                throw new ParserException(ex);
            }
        }
        this.rawData.write(data);
        this.state.parseByte(this, data);
        return finished;
    }

    protected final void setState(ParserState state) {
        this.state = state;
    }

    public String getRawDataAsString() throws UnsupportedEncodingException {
        return new String(this.rawData.toByteArray(), "UTF-8");
    }

}
