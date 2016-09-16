package net.sf.selibs.tcp.factory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import static net.sf.selibs.tcp.factory.ThreadPoolFactory.QueueType.SYNCHRONOUS;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
@EqualsAndHashCode
@ToString
public class ThreadPoolFactory {

    @Element
    public int minThreads = 10;
    @Element
    public int maxThreads = 10;
    @Element
    public QueueType queueType=SYNCHRONOUS;
    @Element(required = false)
    public Integer queueLength;
    @Element
    public int keepAliveTime=60;

    public ThreadPoolExecutor produce() {
        BlockingQueue<Runnable> queue = null;
        switch (queueType) {
            case SYNCHRONOUS:
                queue = new SynchronousQueue();
                break;
            case LINKED:
                if (this.queueLength != null) {
                    queue = new LinkedBlockingQueue(this.queueLength);
                } else {
                    queue = new LinkedBlockingQueue();
                }
                break;
        }
        return new ThreadPoolExecutor(this.minThreads, this.maxThreads, this.keepAliveTime, TimeUnit.SECONDS, queue);
    }

    public enum QueueType {

        SYNCHRONOUS, LINKED;
    }

}
