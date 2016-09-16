package net.sf.selibs.utils.service;

public interface AbstractService {

    public void start() throws Exception;

    public void stop();

    public void join() throws InterruptedException;
}
