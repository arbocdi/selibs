package net.sf.selibs.utils.io.streams;

import java.io.IOException;
import java.io.InputStream;

public class CountingInputStream extends InputStream {

    protected int limit;
    protected InputStream in;
    //=======================
    protected volatile int bytesRead = 0;

    public CountingInputStream(int limit, InputStream in) {
        this.limit = limit;
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        this.bytesRead++;
        if (this.bytesRead > this.limit) {
            throw new IOException(String.format("Read limit %s exceeded", this.limit));
        }
        return in.read();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void reset() {
        this.bytesRead = 0;
    }
}
