package net.sf.selibs.utils.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Slf4j
@Root
public abstract class Service implements AbstractService {

    //config===============
    @Setter
    @Getter
    @Element(name = "sleepInterval")
    protected volatile int sleepInterval;
    //work=================
    @Getter
    protected Thread worker;
    @Getter
    protected volatile boolean stopped = true;

    @Override
    public synchronized void start() throws ServiceException {
        if (!this.stopped) {
            throw new ServiceException(String.format("Service %s is already running ", this.getClass().getName()));
        } else {
            log.info(String.format("Service %s starting", this.getClass().getName()));
            this.stopped = false;
            this.worker = new Thread(this.getClass().getName()) {
                @Override
                public void run() {
                    try {
                        preAction();
                        while (!stopped) {
                            doStuff();
                            if (stopped) {
                                return;
                            }
                            try {
                                Thread.sleep(sleepInterval);
                            } catch (Exception ex) {
                            }
                        }
                    } catch (Exception ex) {
                        log.warn(String.format("Error occured while running %s", this.getClass().getName()), ex);
                    } finally {
                        postAction();
                        Service.this.stop(false);
                    }
                }
            };
            this.worker.start();
        }
    }

    @Override
    public void stop() {
        this.stop(true);
    }

    public synchronized void stop(boolean interrupt) {
        log.info(String.format("Service %s stopping", this.getClass().getName()));
        this.stopped = true;
        if (this.worker != null && interrupt) {
            this.worker.interrupt();
        }
    }

    protected abstract void doStuff() throws Exception;

    protected void preAction() throws Exception {

    }

    protected void postAction() {

    }

    @Override
    public void join() throws InterruptedException {
        this.getWorker().join();
    }

}
