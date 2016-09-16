package net.sf.selibs.orm.relations;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import lombok.Data;

@Data
public class StarID {
    @GeneratedValue
    @Column(columnDefinition = "serial NOT NULL")
    public int id;
}
