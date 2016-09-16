package net.sf.selibs.http.constants;

public interface ParserConstants {
    byte[] END_HEADER = new byte[]{'\r', '\n', '\r', '\n'};
    byte[] END_CHUNKS = new byte[]{'0', '\r', '\n', '\r', '\n'};
}
