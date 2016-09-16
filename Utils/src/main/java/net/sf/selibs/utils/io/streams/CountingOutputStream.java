package net.sf.selibs.utils.io.streams;

import java.io.IOException;
import java.io.OutputStream;
import lombok.Getter;

public class CountingOutputStream extends OutputStream {

    protected OutputStream out;
    @Getter
    protected long bytesWritten = 0;

    public CountingOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        this.bytesWritten++;
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    public void reset() {
        this.bytesWritten = 0;
    }

}
