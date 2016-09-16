package net.sf.selibs.utils.locator;

import java.io.Serializable;

public interface ServiceName extends Serializable {

    String getName();

    @Override
    int hashCode();

    @Override
    boolean equals(Object o);
}
