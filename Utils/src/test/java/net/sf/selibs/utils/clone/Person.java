package net.sf.selibs.utils.clone;

import java.io.Serializable;
import lombok.Data;

@Data
public class Person implements Serializable {

    public String name;
    public int age;
}
