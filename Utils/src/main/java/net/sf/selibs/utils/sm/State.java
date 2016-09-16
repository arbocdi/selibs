package net.sf.selibs.utils.sm;

import java.io.Serializable;


public interface State<C extends Context> extends Serializable {
    void goNext(C c) throws Exception;
}
