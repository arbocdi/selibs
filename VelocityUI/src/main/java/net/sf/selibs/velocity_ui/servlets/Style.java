package net.sf.selibs.velocity_ui.servlets;

import lombok.Data;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Data
@Root
public class Style {

    @Element
    public String headerBGcolor;
    @Element
    public String headerColor;
    @Element
    public String hoverColor;
    @Element
    public String dataTable2n1Color;
    @Element
    public String activeColor;


    public static Style getGreyStyle() {
        Style style = new Style();
        style.headerBGcolor = "#333";
        style.headerColor = "#fff";
        style.hoverColor = "#111";
        style.dataTable2n1Color = "#ccc";
        style.activeColor="#4CAF50";
        return style;
    }
}
