package net.sf.selibs.utils.inject;

import java.io.File;
import net.sf.selibs.utils.misc.USerializer;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

public class ISerializer extends USerializer<Injector, File> {

    public ISerializer(File source) {
        super(source);
    }

    @Override
    public void save(Injector object) throws Exception {
        Strategy strategy = new CycleStrategy("id", "ref");
        Serializer persister = new Persister(strategy);
        persister.write(object.bindings, storage);
    }

    @Override
    public Injector load() throws Exception {
        Strategy strategy = new CycleStrategy("id", "ref");
        Serializer persister = new Persister(strategy);
        Injector i = new Injector();
        i.bindings = persister.read(Injector.Bindings.class, storage);
        return i;
    }

}
