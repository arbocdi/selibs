package net.sf.selibs.http.nio;

/**
 *
 * @author ashevelev
 */
public interface ParserState {

    public void parseByte(ParserContext ctx, byte data) throws ParserException;
}
