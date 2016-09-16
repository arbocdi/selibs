package net.sf.selibs.orm.cache;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Ship {
    @EmbeddedId
    public ShipID id;
    public String name;
}
