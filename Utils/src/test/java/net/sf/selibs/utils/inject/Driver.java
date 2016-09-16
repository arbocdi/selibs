package net.sf.selibs.utils.inject;

import lombok.Data;

@Data
public class Driver {

    private String name;

    public Driver(String name) {
        this.name = name;
    }
}
