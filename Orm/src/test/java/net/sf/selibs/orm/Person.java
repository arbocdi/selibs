package net.sf.selibs.orm;

import javax.persistence.EmbeddedId;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Person {
    @EmbeddedId
    public PersonID id;
    public  String firstName;
    public String lastName;
}
