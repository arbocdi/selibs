package net.sf.selibs.utils.clone;

import java.io.Serializable;
import net.sf.selibs.utils.cloning.NotCloneable;


@NotCloneable
public class NCPerson implements Serializable {
    public String name;
}
