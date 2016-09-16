package net.sf.selibs.messaging.sync;

/**
 * Закрываемая-открываемая очередь нулевого размера. Положить или взять из
 * закрытой очереди нельзя.
 *
 *
 * @author ashevelev
 */
public class MQueue {

    //state=============
    private Object object;
    private boolean closed = false;
    //monitors==========
    private final Object enqueueMonitor = new Object();

    protected void checkClosed() throws ClosedException {
        if (this.closed) {
            throw new ClosedException("Queue is closed");
        }
    }

    protected void waitIfFull() throws ClosedException, InterruptedException {
        this.checkClosed();
        while (this.object != null) {
            this.wait();
            this.checkClosed();

        }
    }

    protected void waitIfEmpty() throws ClosedException, InterruptedException {
        this.checkClosed();
        while (this.object == null) {
            this.wait();
            this.checkClosed();
        }
    }

    public void enqueue(Object object) throws ClosedException, InterruptedException {
        synchronized (enqueueMonitor) {
            synchronized (this) {
                if (object == null) {
                    throw new NullPointerException("Cant enqueue null object");
                }
                this.waitIfFull();
                this.object = object;
                this.notifyAll();
                this.waitIfFull();
            }
        }
    }

    public synchronized Object dequeue() throws ClosedException, InterruptedException {
        this.waitIfEmpty();
        Object object = this.object;
        this.object = null;
        this.notifyAll();
        return object;
    }

    public synchronized void open() {
        this.closed = false;
    }

    public synchronized void close() {
        this.closed = true;
        this.object = null;
        this.notifyAll();
    }
}
