package net.sf.selibs.utils.io.streams;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;


public class OOSWrapper extends OutputStream{
    protected CountingOutputStream out;
    protected ObjectOutputStream objectOut;
    @Getter
    @Setter
    protected long limit = 1*1024*1024;
    public OOSWrapper(OutputStream out) throws IOException{
        this.out = new CountingOutputStream(out);
        this.objectOut = new ObjectOutputStream(this.out);
        this.objectOut.flush();
    }
    
    public void writeObject(Serializable object) throws IOException{
       this.objectOut.writeObject(object);
       this.objectOut.flush();
       if(this.out.getBytesWritten()>=limit){
            this.objectOut.reset();
            this.out.reset();
        }
    }
    
    @Override
    public void write(int b) throws IOException {
        this.objectOut.write(b);
    }
    @Override
    public void flush() throws IOException {
        this.objectOut.flush();
    }
    @Override
    public void close() throws IOException{
        this.objectOut.close();
    }
    
}
