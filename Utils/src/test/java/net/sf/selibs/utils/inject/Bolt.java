package net.sf.selibs.utils.inject;

import javax.inject.Inject;
import javax.inject.Named;
import lombok.Data;

@Data
public class Bolt {

    @Inject
    @Named("bm")
    private Manufacturer m;
}
