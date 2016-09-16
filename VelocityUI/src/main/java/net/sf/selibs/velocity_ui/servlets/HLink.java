package net.sf.selibs.velocity_ui.servlets;

import lombok.Data;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Data
@Root
public class HLink {

    @Element(name = "path")
    protected String path;
    @Element(name = "name")
    protected String name;
    protected boolean active;

    public HLink(@Element(name = "path") String path,
            @Element(name = "name") String name) {
        this.path = path;
        this.name = name;

    }

    public String getClassAttr() {
        if (active) {
            return "class=\"a_button a_button_active\"";
        } else {
            return "class=\"a_button\"";
        }
    }

    @Override
    public HLink clone() {
        return new HLink(path, name);
    }

}
