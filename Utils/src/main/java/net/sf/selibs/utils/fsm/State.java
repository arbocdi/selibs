package net.sf.selibs.utils.fsm;

public interface State<E extends AbstractContext> {

    void processState(E e) throws Exception;
}
