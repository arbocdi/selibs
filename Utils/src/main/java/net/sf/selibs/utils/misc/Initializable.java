package net.sf.selibs.utils.misc;

public interface Initializable<I> {

    void init() throws Exception;

    void inject(I injector) throws Exception;
}
