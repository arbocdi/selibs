package net.sf.selibs.http.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * block-len/r/nblock-data/r/n
 *
 * @author root
 */
public class ChunkedInputStream extends InputStream {

    protected static final int NO_RETURN_BYTE = -10;

    protected InputStream in;
    protected State state;
    protected BlockLength blockLength;
    protected BlockData blockData;

    public ChunkedInputStream(InputStream in) {
        this.in = in;
        blockLength = new BlockLength();
        blockData = new BlockData();
        this.setState(this.blockLength);
    }

    @Override
    public int read() throws IOException {
        while (true) {
            int bt = this.state.readByte(this, in);
            switch (bt) {
                case (NO_RETURN_BYTE):
                    break;
                default:
                    return bt;
            }

        }

    }

    protected final void setState(State state) {
        this.state = state;
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    public static interface State {

        int readByte(ChunkedInputStream chin, InputStream in) throws IOException;
    }

    public static class BlockLength implements State {

        //hex-len/r/n
        protected ByteArrayOutputStream blockLenRaw = new ByteArrayOutputStream();
        protected int blockLen;

        @Override
        public int readByte(ChunkedInputStream chin, InputStream in) throws IOException {
            int bt = in.read();
            switch (bt) {
                case (-1):
                    return bt;
                case ('\r'):
                    //skip byte
                    return NO_RETURN_BYTE;
                case ('\n'):
                    //skip byte,blockLen available
                    blockLen = Integer.parseInt(new String(blockLenRaw.toByteArray(), "UTF-8"), 16);
                    blockLenRaw = new ByteArrayOutputStream();
                    chin.setState(chin.blockData);
                    //starting to read new block
                    chin.blockData.byteCounter = 0;
                    if (blockLen == 0) {
                        return -1;
                    } else {
                        return NO_RETURN_BYTE;
                    }
                default:
                    //add byte to blockLen
                    blockLenRaw.write(bt);
                    return NO_RETURN_BYTE;
            }
        }

    }

    public static class BlockData implements State {
        //block-data/r/n

        protected int byteCounter;

        @Override
        public int readByte(ChunkedInputStream chin, InputStream in) throws IOException {
            int bt = in.read();
            if (bt == -1) {
                return bt;
            }
            byteCounter++;
            if (byteCounter <= chin.blockLength.blockLen) {
                return bt;
            } else {
                in.read();
                in.read();
                return NO_RETURN_BYTE;
            }
        }

    }

}
