package net.sf.selibs.orm.cache;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Character {
    @EmbeddedId
    public CharacterID id;
    public String name;
}
