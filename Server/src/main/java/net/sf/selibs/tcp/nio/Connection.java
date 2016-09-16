package net.sf.selibs.tcp.nio;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.utils.misc.SyncCounter;

public class Connection {

    protected static final SyncCounter idGenerator = new SyncCounter();
    @Getter
    @Setter
    protected volatile SocketChannel channel;
    @Getter
    protected final Queue queue = new LinkedBlockingQueue();
    @Getter
    protected final Map attachments = new ConcurrentHashMap();
    @Getter
    protected final int id;

    public Connection() {
        id = idGenerator.increment();
    }

    public Connection(SocketChannel channel) {
        this();
        this.channel = channel;
    }

    public Connection(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Connection other = (Connection) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {

        return "Connection{" + "id=" + id + '}';
    }

    public String getRemoteAddress() {
        String remote = "";
        try {
            if(this.channel!=null){
                remote = this.channel.getRemoteAddress().toString();
            }

        } catch (Exception ignored) {
        }

        return remote;
    }
    public String getLocalAddress() {
        String remote = "";
        try {
            if(this.channel!=null){
                remote = this.channel.getLocalAddress().toString();
            }

        } catch (Exception ignored) {
        }

        return remote;
    }
    
}
