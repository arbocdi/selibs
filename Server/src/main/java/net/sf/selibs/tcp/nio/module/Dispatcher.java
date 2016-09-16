package net.sf.selibs.tcp.nio.module;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.tcp.nio.ConnectionListener;
import net.sf.selibs.tcp.nio.Processor;
import net.sf.selibs.tcp.nio.Utils;
import net.sf.selibs.utils.graph.Node;
import net.sf.selibs.utils.misc.UHelper;
import net.sf.selibs.utils.service.Service;
import net.sf.selibs.utils.service.ServiceException;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Slf4j
@Root
public class Dispatcher extends Service {

    //config===============
    @Setter
    @Getter
    @Element(required=false)
    @Node
    protected Processor processor;
    @Getter
    @Setter
    @Element(required = false)
    @Node
    protected ConnectionListener listener;
    //work=================
    protected Selector selector;
    protected final ByteBuffer bb = ByteBuffer.allocate(2048);
    protected final Object guard = new Object();
    protected final ConcurrentHashMap<Integer, Connection> connections = new ConcurrentHashMap();
    public int testCounter=0;

    public Dispatcher() {

    }

    public Collection<Connection> getConnections() {
        return this.connections.values();
    }

    protected void putConnection(Connection con) {
        synchronized (guard) {
            this.connections.put(con.getId(), con);
        }
    }

    protected void removeConnection(int id) {
        synchronized (guard) {
            this.connections.remove(id);
        }
    }

    public Connection getConnection(int id) {
        synchronized (guard) {
            return this.connections.get(id);
        }
    }

    public void register(Connection con) {
        this.listener.connected(con);
        synchronized (guard) {
            this.putConnection(con);
            this.selector.wakeup();
            try {
                con.getChannel().configureBlocking(false);
                con.getChannel().register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, con);
                //this.registered.increment();
            } catch (Exception ex) {
                log.debug(String.format("Error while registering on selector %s", con), ex);
                this.close(con);
            }
        }
    }

    @Override
    protected void doStuff() {
        synchronized (guard) {
        }
        try {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> keysI = keys.iterator();
            this.processKeys(keysI);
            //this.processKeys(selector.keys().iterator());
        } catch (Exception ex) {
            log.debug("Error while selecting...", ex);
        }

    }

    protected void processKeys(Iterator<SelectionKey> keys) {
        while (keys.hasNext()) {
            SelectionKey key = keys.next();
            keys.remove();
            if (key.isValid() && key.isReadable()) {
                this.read(key);
            }
            if (key.isValid() && key.isWritable()) {
                
                this.write(key);
            }
        }
    }

    protected void write(SelectionKey key) {
        //testCounter++;
        SocketChannel channel = (SocketChannel) key.channel();
        Connection con = (Connection) key.attachment();
        try {
            //выходим из цикла если нет данных для записи либо если канал больше принять не может

            Queue writeQueue = con.getQueue();
            while (true) {
                ByteBuffer bb = null;
                synchronized (guard) {
                    bb = (ByteBuffer) writeQueue.peek();
                    //если нет данных для записи пометим канал заинтересованным только для чтения

                    if (bb == null) {
                        key.interestOps(SelectionKey.OP_READ);
                        return;
                    }
                }
                //if recieved 0-length buffer - close channel
                if (bb.capacity() == 0) {
                    this.close(con);
                    return;
                }
                channel.write(bb);
                //когда весь буфер записан удалим его из очереди на запись
                if (bb.hasRemaining()) {
                    return;
                } else {
                    writeQueue.poll();
                }

            }
        } catch (Exception ex) {
            log.debug(String.format("Exception occured while writing data to client %s", con), ex);
            this.close(con);
        }
        //  }
    }

    protected void read(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        Connection con = (Connection) key.attachment();
        try {
            this.bb.clear();
            int numRead = channel.read(bb);
            if (numRead == -1) {
                this.close(con);
            } else {
                bb.flip();
                byte[] data = new byte[numRead];
                bb.get(data);
                this.processor.process(data, con, this);
            }
        } catch (Exception ex) {
            log.debug(String.format("Exception occured while reading data from client %s", con), ex);
            this.close(con);
        }
    }

    public void addWriteRequest(Connection con, byte[] data) {
        synchronized (guard) {
            this.selector.wakeup();
            SelectionKey key = con.getChannel().keyFor(selector);
            con.getQueue().offer(ByteBuffer.wrap(data));
            if (key != null) {
                key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            }
        }
    }

    public void addSoftCloseRequest(Connection con) {
        this.addWriteRequest(con, new byte[0]);
    }

    public void close(Connection con) {
        Connection fromStore;
        synchronized (guard) {
            fromStore = this.getConnection(con.getId());
            this.removeConnection(con.getId());
            Utils.close(con, selector);
        }
        if (fromStore != null) {
            this.listener.disconnected(con);
        }
    }

    public void closeAllConnections() {
        synchronized (guard) {
            this.connections.clear();
            Utils.closeAllConnections(selector);
        }
    }

    @Override
    public void start() throws ServiceException {
        try {
            this.selector = Selector.open();
            super.start();
        } catch (Exception ex) {
            this.stop();
            if (this.selector != null) {
                this.postAction();
            }
            throw new ServiceException(ex);
        }

    }

    @Override
    protected synchronized void postAction() {
        this.closeAllConnections();
        UHelper.close(selector);
    }

}
