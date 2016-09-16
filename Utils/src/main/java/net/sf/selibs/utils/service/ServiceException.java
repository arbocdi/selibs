package net.sf.selibs.utils.service;

public class ServiceException extends Exception {

    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException(Exception ex) {
        super(ex);
    }
}
