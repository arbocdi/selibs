package net.sf.selibs.utils.inject;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
public class Manufacturer {

    private String name;

    public Manufacturer(String name) {
        this.name = name;
    }
}
