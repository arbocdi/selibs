package net.sf.selibs.utils.chain;

public class HException extends Exception {

    public HException() {
    }

    public HException(String msg) {
        super(msg);
    }

    public HException(Exception ex) {
        super(ex);
    }

}
