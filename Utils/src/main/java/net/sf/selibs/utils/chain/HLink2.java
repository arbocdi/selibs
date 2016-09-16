package net.sf.selibs.utils.chain;

import org.simpleframework.xml.Root;

@Root
public abstract class HLink2<I, O> extends HLink<I, O> {
    

    @Override
    public O handle(I i) throws Exception {
        O o = this.doHandle(i);
        if (o != null) {
            return o;
        }
        if (next != null) {
            return next.handle(i);
        } else {
            return null;
        }
    }

    public abstract O doHandle(I i) throws HException;

}
