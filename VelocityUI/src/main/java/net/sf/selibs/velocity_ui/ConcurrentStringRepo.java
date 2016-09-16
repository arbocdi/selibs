package net.sf.selibs.velocity_ui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.velocity.runtime.resource.util.StringResource;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

public class ConcurrentStringRepo implements StringResourceRepository {

    protected volatile String encoding;
    protected Map<String,String> resources = new ConcurrentHashMap();

    @Override
    public StringResource getStringResource(String name) {
        String resource=this.resources.get(name);
        return new StringResource(resource, encoding);
    }

    @Override
    public void putStringResource(String name, String body) {
        resources.put(name,body);
    }

    @Override
    public void putStringResource(String name, String body, String encoding) {
        this.putStringResource(name, body);
    }

    @Override
    public void removeStringResource(String name) {
        this.resources.remove(name);
    }

    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String getEncoding() {
        return this.encoding;
    }

}
