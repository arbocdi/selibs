package net.sf.selibs.utils.chain;

import lombok.Getter;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import net.sf.selibs.utils.graph.*;

@Root
public abstract class HLink<I, O> implements Handler<I, O> {

    @Getter
    @Element(required = false)
    @Node
    protected Handler<I, O> next;

    public Handler<I, O> setNext(Handler<I, O> next) {
        this.next = next;
        return next;
    }
}
