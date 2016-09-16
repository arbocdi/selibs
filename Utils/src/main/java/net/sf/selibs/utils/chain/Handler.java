package net.sf.selibs.utils.chain;

import java.io.Serializable;

public interface Handler<I, O> extends Serializable {

    O handle(I i) throws Exception;

}
