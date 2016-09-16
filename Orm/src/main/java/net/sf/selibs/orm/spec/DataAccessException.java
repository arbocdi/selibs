package net.sf.selibs.orm.spec;

public class DataAccessException extends Exception {

    public DataAccessException(Exception ex) {
        super(ex);
    }

    public DataAccessException(String msg) {
        super(msg);
    }
}
