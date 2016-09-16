package net.sf.selibs.orm;

import javax.persistence.GeneratedValue;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class PersonID {
    @GeneratedValue
    public Integer id;
    public String uid;
}
