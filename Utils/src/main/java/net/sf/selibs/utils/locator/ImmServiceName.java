package net.sf.selibs.utils.locator;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
@ToString
@EqualsAndHashCode
public class ImmServiceName implements ServiceName {

    @Element(name = "name")
    public String name;

    public ImmServiceName(@Element(name = "name") String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
